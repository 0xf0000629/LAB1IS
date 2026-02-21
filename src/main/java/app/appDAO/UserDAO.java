package app.appDAO;

import app.HibernateUtil;
import app.appentities.Users;
import app.exceptions.DatabaseException;
import app.exceptions.LoginFailedException;
import app.exceptions.UserNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import javax.security.auth.login.LoginException;
import java.util.List;

@Repository
public class UserDAO {
    // UserDAO is responsible for working with the Users entity



    // this gets a list of Users from DB
    public List<Users> getAllUser() {
        Transaction transaction = null;
        List<Users> user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            user = session.createQuery("from Users", Users.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return user;
    }

    // this returns a Users entity by their ID
    public static Users getUserById(Long id) {
        Transaction transaction = null;
        Users user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            user = session.get(Users.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return user;
    }

    // this returns a Users entity by their username
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
            throw new DatabaseException(e);
        }

        return user;
    }

    // this returns the hashed password if the username is in the DB
    public String grabPassword(String username, String password) {
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
            throw new DatabaseException(e);
        }
        if (user != null) {
            return user.getPassword();
        }
        throw new UserNotFoundException();
    }

    // this persists a Users entity in the DB
    public void saveUser(Users user) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.persist(user);
        transaction.commit();
        session.close();
    }
}
