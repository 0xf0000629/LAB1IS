package app.controller;

import app.HibernateUtil;
import app.appDAO.HumanDAO;
import app.appentities.Human;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/human")
public class HumanController {

    private final HumanDAO humanDAO;

    public HumanController() {
        this.humanDAO = new HumanDAO(); // Instantiate the DAO
    }

    // GET ALL OF EM
    @GetMapping
    public List<Human> getAllCities() {
        return humanDAO.getAllHumans();
    }

    // GET
    @GetMapping("/{id}")
    public Human getHumanById(@PathVariable Long id) {
        Human human = humanDAO.getHumanById(id);
        if (human == null) {
            throw new RuntimeException("Human not found with id: " + id); // Handle this better with proper exception handling
        }
        return human;
    }

    // ADD
    @PostMapping
    public Human addHuman(@RequestBody Human human) {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(human);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error saving human: " + e.getMessage());
        }

        return human;
    }

    // UPDATE
    @PutMapping("/{id}")
    public Human updateHuman(@PathVariable("id") Long id, @RequestBody Human updatedHuman) {
        Human human = humanDAO.getHumanById(id);
        if (human == null) {
            throw new RuntimeException("Human not found with id: " + id);
        }

        human.setName(updatedHuman.getName());
        human.setAge(updatedHuman.getAge());
        human.setHeight(updatedHuman.getHeight());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(human);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error updating human: " + e.getMessage());
        }

        return human;
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteHuman(@PathVariable("id") Long id) {
        Human human = humanDAO.getHumanById(id);
        if (human == null) {
            throw new RuntimeException("Human not found with id: " + id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(human);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting human: " + e.getMessage());
        }

        return "Human with id " + id + " has been deleted.";
    }
}
