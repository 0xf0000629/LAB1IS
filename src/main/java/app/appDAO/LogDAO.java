package app.appDAO;

import app.HibernateUtil;
import app.appentities.Logs;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LogDAO {

    public List<Logs> getAllLogs() {
        Transaction transaction = null;
        List<Logs> logs = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            logs = session.createQuery("from Logs", Logs.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }

        return logs;
    }
    public static void saveLog(Logs logs) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(logs);
        transaction.commit();
        session.close();
    }
}
