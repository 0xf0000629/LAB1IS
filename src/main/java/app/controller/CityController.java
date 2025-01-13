package app.controller;

import app.HibernateUtil;
import app.appDAO.CityDAO;
import app.appDAO.CoordinatesDAO;
import app.appDAO.HumanDAO;
import app.appentities.City;
import app.appentities.Coordinates;
import app.appentities.Human;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public City addCity(@RequestBody City city) {

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
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving city: " + e.getMessage());
        }
        return city;
    }

    // UPDATE
    @PutMapping("/{id}")
    public City updateCity(@PathVariable("id") Long id, @RequestBody City updatedCity) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id);
        }

        city.setName(updatedCity.getName());
        city.setArea(updatedCity.getArea());
        city.setPopulation(updatedCity.getPopulation());
        city.setCapital(updatedCity.isCapital());
        city.setMeters_above_sea_level(updatedCity.getMeters_above_sea_level());
        city.setCar_code(updatedCity.getCar_code());
        city.setClimate(updatedCity.getClimate());
        city.setStandardOfLiving(updatedCity.getStandardOfLiving());
        city.setGovernor(updatedCity.getGovernor());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(city);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating city: " + e.getMessage());
        }

        return city;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteCity(@PathVariable("id") Long id) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(city);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting city: " + e.getMessage());
        }

        return "City with id " + id + " has been deleted.";
    }
}
