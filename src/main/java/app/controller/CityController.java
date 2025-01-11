package app.controller;

import app.HibernateUtil;
import app.appDAO.CityDAO;
import app.appentities.City;
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

    // Get all cities
    @GetMapping
    public List<City> getAllCities() {
        return cityDAO.getAllCities();
    }

    // Get a city by ID
    @GetMapping("/{id}")
    public City getCityById(@PathVariable Long id) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id); // Handle this better with proper exception handling
        }
        return city;
    }

    // Add a new city
    @PostMapping
    public City addCity(@RequestBody City city) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(city);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving city: " + e.getMessage());
        }

        return city;
    }

    // Update an existing city
    @PutMapping("/{id}")
    public City updateCity(@PathVariable Long id, @RequestBody City updatedCity) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new RuntimeException("City not found with id: " + id);
        }

        // Update city fields
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

    // Delete a city
    @DeleteMapping("/{id}")
    public String deleteCity(@PathVariable Long id) {
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
