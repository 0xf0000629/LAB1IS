package app.services;

import app.HibernateUtil;
import app.appDAO.HumanDAO;
import app.appentities.Human;
import app.exceptions.HumanDeletionException;
import app.exceptions.HumanNotFoundException;
import app.exceptions.HumanSavingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/human")
public class HumanService {

    @Autowired
    private final HumanDAO humanDAO;

    public HumanService() {
        this.humanDAO = new HumanDAO();
    }

    // this returns a list of all humans
    public List<Human> getAllHumans() {
        return humanDAO.getAllHumans();
    }

    // this returns a human by ID
    public Human getHumanById(Long id) {
        Human human = humanDAO.getHumanById(id);
        if (human == null) {
            throw new HumanNotFoundException(id);
        }
        return human;
    }

    // this adds a new human
    public Human addHuman(Human human) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(human);
            transaction.commit();
        } catch (Exception e) {
            throw new HumanSavingException(e);
        }

        return human;
    }

    // this updates a human by ID
    public Human updateHuman(Long id, Human updatedHuman) {
        Human human = humanDAO.getHumanById(id);
        if (human == null) {
            throw new HumanNotFoundException(id);
        }

        human.setName(updatedHuman.getName());
        human.setAge(updatedHuman.getAge());
        human.setHeight(updatedHuman.getHeight());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(human);
            transaction.commit();
        } catch (Exception e) {
            throw new HumanSavingException(e);
        }

        return human;
    }

    // this deletes a human by ID
    public String deleteHuman(Long id) {
        Human human = humanDAO.getHumanById(id);
        if (human == null) {
            throw new HumanNotFoundException(id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(human);
            transaction.commit();
        } catch (Exception e) {
            throw new HumanDeletionException(id, e);
        }

        return "Human with id " + id + " has been deleted.";
    }
}
