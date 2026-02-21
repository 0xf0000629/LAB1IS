package app.appDAO;


import app.CacheMe;
import app.HibernateUtil;
import app.appentities.Human;
import app.appentities.Users;
import app.exceptions.DatabaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.QueryHints;
import org.hibernate.jpa.AvailableHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class HumanDAO {

    // HumanDAO is responsible for the Human entity in the DB

    @Autowired
    private SessionFactory sessionFactory;

    @PersistenceContext
    private EntityManager entityManager;

    // this gets a list of all humans in the DB
    @CacheMe
    @Transactional
    public List<Human> getAllHumans() {
        Transaction transaction = null;
        List<Human> humans = null;

        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            humans = session.createQuery("from Human", Human.class).setHint(AvailableHints.HINT_CACHEABLE, true).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return humans;
    }

    // this returns a Human by their ID
    @CacheMe
    @Transactional
    public Human getHumanById(Long id) {
        Transaction transaction = null;
        Human human = null;

        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            human = session.get(Human.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return human;
    }

    // this returns a Human by their name
    public static Human getHumanByName(String name) {
        Transaction transaction = null;
        Human human = null;

        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM Human WHERE name = :name";
            human = session.createQuery(hql, Human.class)
                    .setParameter("name", name)
                    .uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return human;
    }

    // this persists a Human in the DB
    @CacheMe
    @Transactional
    public static void saveHuman(Human human) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.persist(human);
        transaction.commit();
        session.close();
    }
}
