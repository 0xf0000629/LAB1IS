package app.appDAO;

import app.HibernateUtil;
import app.appentities.Users;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

import static app.SecurityUtil.hashPassword;

@Repository
public class UserDAO {

    public List<Users> getAllUser() {
        Transaction transaction = null;
        List<Users> user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            user = session.createQuery("from Users", Users.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return user;
    }

    public static Users getUserById(Long id) {
        Transaction transaction = null;
        Users user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            user = session.get(Users.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return user;
    }
    public Users findByUsername(String username) {
        Transaction transaction = null;
        Users user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM Users WHERE username = :username";
            user = session.createQuery(hql, Users.class)
                    .setParameter("username", username)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return user;
    }

    public boolean confirm(String username, String password) {
        Transaction transaction = null;
        Users user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM Users WHERE username = :username";
            user = session.createQuery(hql, Users.class)
                    .setParameter("username", username)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
        if (user != null) {
            // Hash the provided password
            String hashedPassword = hashPassword(password);
            // Compare the hashed password with the stored hashed password
            return hashedPassword.equals(user.getPassword());
        }

        return false;
    }

    public void saveUser(Users user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(user);
        transaction.commit();
        session.close();
    }
}
