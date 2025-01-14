package app.appDAO;

import app.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class LogDAO {

    public static void savelog(String user, String action, String entity){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "INSERT INTO logs (username, action, nameofentity) \n" +
                    "VALUES ('" + user + "', '" + action + "', '" + entity + "');";
            session.createNativeQuery(sql).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }
    }
}
