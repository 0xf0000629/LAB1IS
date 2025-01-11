package app.appDAO;

import app.HibernateUtil;
import app.appentities.Coordinates;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Repository
public class CoordinatesDAO {


    public void saveCoordinates(Coordinates coords) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(coords);
        transaction.commit();
        session.close();
    }
}
