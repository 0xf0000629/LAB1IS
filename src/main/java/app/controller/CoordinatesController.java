package app.controller;

import app.HibernateUtil;
import app.appDAO.CoordinatesDAO;
import app.appentities.Coordinates;
import app.services.CoordinatesService;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coords")
public class CoordinatesController {

    private final CoordinatesService coordsService;

    public CoordinatesController() {
        this.coordsService = new CoordinatesService(); // Instantiate the service
    }

    // this catches requests for a list of all of the coordinates
    @GetMapping
    public List<Coordinates> getAllCoordinatesRequest() {
        return coordsService.getAllCoordinates();
    }

    // this catches requests for a pair of coordinates by their ID
    @GetMapping("/{id}")
    public Coordinates getCoordinatesByIdRequest(@PathVariable Long id) {
        return coordsService.getCoordinatesById(id);
    }

    // this catches requests for adding a pair of coordinates
    @PostMapping
    public Coordinates addCoordinatesRequest(@RequestBody Coordinates coords) {
        return coordsService.addCoordinates(coords);
    }

    // this catches requests for updating a pair of a coordinates
    @PutMapping("/{id}")
    public Coordinates updateCoordinatesRequest(@PathVariable Long id, @RequestBody Coordinates updatedCoordinates) {
        return coordsService.updateCoordinates(id, updatedCoordinates);
    }

    // this catches requests for deleting a pair of a coordinates
    @DeleteMapping("/{id}")
    public String deleteCoordinatesRequest(@PathVariable Long id) {
        return coordsService.deleteCoordinates(id);
    }
}
