package app.controller;

import app.HibernateUtil;
import app.appDAO.HumanDAO;
import app.appentities.Human;
import app.services.HumanService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/human")
public class HumanController {

    @Autowired
    private final HumanService humanService;

    public HumanController() {
        this.humanService = new HumanService();
    }

    // this catches requests for a list of all of the humans
    @GetMapping
    public List<Human> getAllHumansRequest() {
        return humanService.getAllHumans();
    }

    // this catches requests for a human by their ID
    @GetMapping("/{id}")
    public Human getHumanByIdRequest(@PathVariable("id") Long id) {
        return humanService.getHumanById(id);
    }

    // this catches requests for adding a human
    @PostMapping
    public Human addHumanRequest(@RequestBody Human human) {
        return humanService.addHuman(human);
    }

    // this catches requests for updating a human by their ID
    @PutMapping("/{id}")
    public Human updateHumanRequest(@PathVariable("id") Long id, @RequestBody Human updatedHuman) {
        return humanService.updateHuman(id, updatedHuman);
    }

    // this catches requests for deleting a human by their ID
    @DeleteMapping("/{id}")
    public String deleteHumanRequest(@PathVariable("id") Long id) {
        return humanService.deleteHuman(id);
    }
}
