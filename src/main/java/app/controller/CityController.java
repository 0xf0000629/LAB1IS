package app.controller;

import app.HibernateUtil;
import app.appDAO.*;
import app.appentities.*;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.controller.LoginController.extractUsername;

@RestController
@RequestMapping("/api/cities")
public class CityController {

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

        Coordinates coordinates = new Coordinates();
        coordinates.setX(city.getCoordinates().getX());
        coordinates.setY(city.getCoordinates().getY());
        CoordinatesDAO.saveCoordinates(coordinates);
        city.setCoordinates(coordinates); // Associate coordinates

        // Reuse existing governor if ID is provided
        if (city.getGovernor() != null && city.getGovernor().getId() != null) {
            Human governor = HumanDAO
                    .getHumanById(city.getGovernor().getId());
            city.setGovernor(governor);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(city);
            LogDAO.savelog(getUsername(request),"ADDED",city.getName());
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

        city.setName(updatedCity.getName());
        city.setArea(updatedCity.getArea());
        city.setPopulation(updatedCity.getPopulation());
        city.setEstablishment_date(updatedCity.getEstablishment_date());
        city.setCapital(updatedCity.isCapital());
        city.setMeters_above_sea_level(updatedCity.getMeters_above_sea_level());
        city.setCar_code(updatedCity.getCar_code());
        city.setClimate(updatedCity.getClimate());
        city.setStandardOfLiving(updatedCity.getStandardOfLiving());
        city.setGovernor(updatedCity.getGovernor());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(city);
            LogDAO.savelog(getUsername(request),"UPDATED",city.getName());
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
    public ResponseEntity<String> massive(HttpServletRequest request, @RequestBody MyBigFatPayload payload){
        List<City> cities = payload.getCities();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Map<String, Human> govns = new HashMap<String,Human>();
            for (int i=0;i<cities.size();i++) {
                City city = cities.get(i);
                Coordinates coordinates = new Coordinates();
                coordinates.setX(city.getCoordinates().getX());
                coordinates.setY(city.getCoordinates().getY());
                CoordinatesDAO.saveCoordinates(coordinates);
                city.setCoordinates(coordinates);

                Human governor = govns.get(city.getGovernor().getName());
                if (governor != null) {
                    city.setGovernor(governor);
                } else {
                    governor = new Human();
                    governor.setName(city.getGovernor().getName());
                    governor.setAge(city.getGovernor().getAge());
                    governor.setHeight(city.getGovernor().getHeight());
                    session.save(governor);
                    govns.put(governor.getName(), governor);
                    city.setGovernor(governor);
                }

                session.save(city);
            }
            LogDAO.savelog(getUsername(request),"MASS ADDITION", ""+cities.size());
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving city: " + e.getMessage());
        }
        return ResponseEntity.ok("yeah epic");
    }
}
