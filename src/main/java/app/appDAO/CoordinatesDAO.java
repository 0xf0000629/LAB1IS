package app.appDAO;

import app.HibernateUtil;
import app.appentities.Coordinates;
import app.exceptions.DatabaseException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Repository
public class CoordinatesDAO {

    // CoordinatesDAO is a DAO responsible for coordinates in the database

    // this gets a list with all of the coordinates
    public List<Coordinates> getAllCoordinates() {
        Transaction transaction = null;
        List<Coordinates> coords = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            coords = session.createQuery("from Coordinates", Coordinates.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return coords;
    }

    // this gets a pair of coordinates by their ID
    public Coordinates getCoordinatesById(Long id) {
        Transaction transaction = null;
        Coordinates coords = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            coords = session.get(Coordinates.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return coords;
    }

    // this persists one pair of coordinates
    public static void saveCoordinates(Coordinates coords) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(coords);
        transaction.commit();
        session.close();
    }
}
