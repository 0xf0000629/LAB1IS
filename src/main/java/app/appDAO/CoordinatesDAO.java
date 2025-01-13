package app.appDAO;

import app.HibernateUtil;
import app.appentities.Coordinates;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Repository
public class CoordinatesDAO {

    public List<Coordinates> getAllCoordinates() {
        Transaction transaction = null;
        List<Coordinates> coords = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            coords = session.createQuery("from Coordinates", Coordinates.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return coords;
    }

    public static Coordinates getCoordinatesById(Long id) {
        Transaction transaction = null;
        Coordinates coords = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            coords = session.get(Coordinates.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return coords;
    }

    public static void saveCoordinates(Coordinates coords) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(coords);
        transaction.commit();
        session.close();
    }
}
