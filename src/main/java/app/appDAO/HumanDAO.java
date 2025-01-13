package app.appDAO;


import app.HibernateUtil;
import app.appentities.Human;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Repository
public class HumanDAO {

    public List<Human> getAllHumans() {
        Transaction transaction = null;
        List<Human> humans = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            humans = session.createQuery("from Human", Human.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return humans;
    }

    public static Human getHumanById(Long id) {
        Transaction transaction = null;
        Human human = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            human = session.get(Human.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return human;
    }

    public void saveHuman(Human human) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(human);
        transaction.commit();
        session.close();
    }
}
