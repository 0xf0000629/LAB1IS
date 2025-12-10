package app.appDAO;

import app.CacheMe;
import app.HibernateUtil;
import app.appentities.Logs;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class LogDAO {

    @Autowired
    private SessionFactory sessionFactory;
    @CacheMe
    @Transactional
    public List<Logs> getAllLogs() {
        Transaction transaction = null;
        List<Logs> logs = null;

        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            logs = session.createQuery("from Logs", Logs.class).setHint(QueryHints.CACHEABLE, true).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }

        return logs;
    }
    @CacheMe
    @Transactional
    public static void saveLog(Logs logs) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.save(logs);
        transaction.commit();
        session.close();
    }
}
