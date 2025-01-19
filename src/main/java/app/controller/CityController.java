package app.controller;

import app.HibernateUtil;
import app.appDAO.*;
import app.appentities.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.controller.LoginController.extractUsername;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private final CityDAO cityDAO;


    public CityController() {
        this.cityDAO = new CityDAO(); // Instantiate the DAO
    }

    // GET ALL OF EM
    @GetMapping
    public List<City> getAllCities() {
        return cityDAO.getAllCities();
    }

    // GET
    @GetMapping("/{id}")
    public City getCityById(@PathVariable("id") Long id) {
        System.out.println("tryna show the city rn");
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id); // Handle this better with proper exception handling
        }
        return city;
    }

    // ADD
    @PostMapping
    public City addCity(HttpServletRequest request, @RequestBody City city) {

        if (CarCodeEnforcer.getArray(city.getCar_code().intValue())){
            throw new RuntimeException("Error saving city: this car code is already in use.");
        }
        Coordinates coordinates = new Coordinates();
        coordinates.setX(city.getCoordinates().getX());
        coordinates.setY(city.getCoordinates().getY());
        CoordinatesDAO.saveCoordinates(coordinates);
        city.setCoordinates(coordinates); // Associate coordinates

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            if (city.getGovernor() != null){
                if (city.getGovernor().getId() != null) {
                    Human governor = HumanDAO
                            .getHumanById(city.getGovernor().getId());
                    city.setGovernor(governor);
                }
                else {
                    Human governor = new Human();
                    governor.setName(city.getGovernor().getName());
                    governor.setAge(city.getGovernor().getAge());
                    governor.setHeight(city.getGovernor().getHeight());
                    session.save(governor);
                    city.setGovernor(governor);
                }
            }
            session.save(city);
            LogDAO.savelog(getUsername(request),"ADDED",city.getName());
            CarCodeEnforcer.setid(city.getCar_code().intValue(), true);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving city: " + e.getMessage());
        }
        return city;
    }

    // UPDATE
    @PutMapping("/{id}")
    public City updateCity(HttpServletRequest request, @PathVariable("id") Long id, @RequestBody City updatedCity) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id);
        }

        if (updatedCity.getName() != null) city.setName(updatedCity.getName());
        if (updatedCity.getArea() != null) city.setArea(updatedCity.getArea());
        if (updatedCity.getPopulation() != null) city.setPopulation(updatedCity.getPopulation());
        if (updatedCity.getEstablishment_date() != null) city.setEstablishment_date(updatedCity.getEstablishment_date());
        if (updatedCity.isCapital() != null) city.setCapital(updatedCity.isCapital());
        if (updatedCity.getMeters_above_sea_level() != null) city.setMeters_above_sea_level(updatedCity.getMeters_above_sea_level());
        if (updatedCity.getCar_code() != null) {
            if (CarCodeEnforcer.getArray(updatedCity.getCar_code().intValue())){
                throw new RuntimeException("Error saving city: this car code is already in use.");
            }
            else
                city.setCar_code(updatedCity.getCar_code());
        }
        if (updatedCity.getClimate() != null) city.setClimate(updatedCity.getClimate());
        if (updatedCity.getStandardOfLiving() != null) city.setStandardOfLiving(updatedCity.getStandardOfLiving());
        if (updatedCity.getGovernor() != null) city.setGovernor(updatedCity.getGovernor());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(city);
            LogDAO.savelog(getUsername(request),"UPDATED",city.getName());
            CarCodeEnforcer.setid(city.getCar_code().intValue(), true);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating city: " + e.getMessage());
        }

        return city;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteCity(HttpServletRequest request, @PathVariable("id") Long id) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(city);
            LogDAO.savelog(getUsername(request),"DELETED",city.getName());
            CarCodeEnforcer.setid(city.getCar_code().intValue(),false);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting city: " + e.getMessage());
        }

        return "City with id " + id + " has been deleted.";
    }

    public String getUsername(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token from the header
            String token = authorizationHeader.substring(7);
            String username = extractUsername(token);

            return username;
        }
        else
            return "<unknown>";
    }

    @GetMapping("/avrgMASL")
    public Double getavrgMASL() {
        return CityDAO.getavrgMASL();
    }
    @GetMapping("/minSOL")
    public List<String> getCitiesWithSOL(@RequestParam("minSOL") String minSOL) {
        System.out.println("GOT " + minSOL);
        return CityDAO.getCitiesWithSOL(minSOL);
    }
    @GetMapping("/uniqueC")
    public List<String> getCitiesWithStandardOfLivingAbove() {
        return CityDAO.getCitiesWithClimate();
    }

    @GetMapping("/toanother")
    public void toanother(@RequestParam("id1") int id1, @RequestParam("id2") int id2) {
        System.out.println("GOT " + id1 + " " + id2);
        CityDAO.transfertoanother(id1, id2);
    }

    @GetMapping("/tosmallest")
    public void tosmallest(@RequestParam("id1") int id1) {
        System.out.println("GOT " + id1);
        CityDAO.transfertosmallest(id1);
    }

    @PostMapping("/mass")
    public ResponseEntity<Map<String, List <Long> >> massive(HttpServletRequest request, @RequestBody MyBigFatPayload payload){
        List<City> cities = payload.getCities();
        List <Long> cityids = cityDAO.citiessaveALL(cities);
        LogDAO.savelog(getUsername(request),"MASS ADDITION", ""+cities.size());
        Map<String, List<Long> > response = new HashMap<>();
        response.put("ids", cityids);
        return ResponseEntity.ok(response);
    }
}
