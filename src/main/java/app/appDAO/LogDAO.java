package app.appDAO;

import app.CacheMe;
import app.HibernateUtil;
import app.appentities.Logs;
import app.exceptions.DatabaseException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.QueryHints;
import org.hibernate.jpa.AvailableHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class LogDAO {

    // LogDAO is responsible for working with the Logs entity in the DB
    @Autowired
    private SessionFactory sessionFactory;

    // this gets a list of Logs from DB
    @CacheMe
    @Transactional
    public List<Logs> getAllLogs() {
        Transaction transaction = null;
        List<Logs> logs = null;

        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            logs = session.createQuery("from Logs", Logs.class).setHint(AvailableHints.HINT_CACHEABLE, true).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return logs;
    }

    // this persists a Logs entity
    @CacheMe
    @Transactional
    public static void saveLog(Logs logs) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.persist(logs);
        transaction.commit();
        session.close();
    }
}
