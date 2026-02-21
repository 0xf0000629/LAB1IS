package app.services;

import app.HibernateUtil;
import app.appDAO.CoordinatesDAO;
import app.appentities.Coordinates;
import app.exceptions.CoordinatesDeletionException;
import app.exceptions.CoordinatesNotFoundException;
import app.exceptions.CoordinatesSavingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Service
public class CoordinatesService {

    private final CoordinatesDAO coordsDAO;

    public CoordinatesService() {
        this.coordsDAO = new CoordinatesDAO(); // Instantiate the DAO
    }

    // this gets a list of all of the coordinates
    public List<Coordinates> getAllCoordinates() {
        return coordsDAO.getAllCoordinates();
    }

    // this gets a pair of coordinates by their ID
    public Coordinates getCoordinatesById(Long id) {
        Coordinates coords = coordsDAO.getCoordinatesById(id);
        if (coords == null) {
            throw new CoordinatesNotFoundException(id);
        }
        return coords;
    }

    // this adds a pair of coordinates
    public Coordinates addCoordinates(Coordinates coords) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(coords);
            transaction.commit();
        } catch (Exception e) {
            throw new CoordinatesSavingException(e);
        }

        return coords;
    }

    // this updates a pair of coordinates by their ID
    public Coordinates updateCoordinates(Long id, Coordinates updatedCoordinates) {
        Coordinates coords = coordsDAO.getCoordinatesById(id);
        if (coords == null) {
            throw new CoordinatesNotFoundException(id);
        }

        coords.setX(updatedCoordinates.getX());
        coords.setY(updatedCoordinates.getY());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(coords);
            transaction.commit();
        } catch (Exception e) {
            throw new CoordinatesSavingException(e);
        }

        return coords;
    }

    // this deletes a pair of coordinates by their ID
    public String deleteCoordinates(Long id) {
        Coordinates coords = coordsDAO.getCoordinatesById(id);
        if (coords == null) {
            throw new CoordinatesNotFoundException(id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(coords);
            transaction.commit();
        } catch (Exception e) {
            throw new CoordinatesDeletionException(id);
        }

        return "Coords with id " + id + " has been deleted.";
    }
}

