package app.controller;

import app.HibernateUtil;
import app.appDAO.CoordinatesDAO;
import app.appentities.Coordinates;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coords")
public class CoordinatesController {

    private final CoordinatesDAO coordsDAO;

    public CoordinatesController() {
        this.coordsDAO = new CoordinatesDAO(); // Instantiate the DAO
    }

    // GET ALL OF EM
    @GetMapping
    public List<Coordinates> getAllCities() {
        return coordsDAO.getAllCoordinates();
    }

    // GET
    @GetMapping("/{id}")
    public Coordinates getCoordinatesById(@PathVariable Long id) {
        Coordinates coords = coordsDAO.getCoordinatesById(id);
        if (coords == null) {
            throw new RuntimeException("Coords not found with id: " + id); // Handle this better with proper exception handling
        }
        return coords;
    }

    // ADD
    @PostMapping
    public Coordinates addCoordinates(@RequestBody Coordinates coords) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(coords);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving coords: " + e.getMessage());
        }

        return coords;
    }

    // UPDATE
    @PutMapping("/{id}")
    public Coordinates updateCoordinates(@PathVariable Long id, @RequestBody Coordinates updatedCoordinates) {
        Coordinates coords = coordsDAO.getCoordinatesById(id);
        if (coords == null) {
            throw new RuntimeException("Coords not found with id: " + id);
        }

        coords.setX(updatedCoordinates.getX());
        coords.setY(updatedCoordinates.getY());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(coords);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating coords: " + e.getMessage());
        }

        return coords;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteCoordinates(@PathVariable Long id) {
        Coordinates coords = coordsDAO.getCoordinatesById(id);
        if (coords == null) {
            throw new RuntimeException("Coords not found with id: " + id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(coords);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting coords: " + e.getMessage());
        }

        return "Coords with id " + id + " has been deleted.";
    }
}
