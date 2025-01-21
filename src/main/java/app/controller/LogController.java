package app.controller;

import app.appDAO.LogDAO;
import app.appentities.Logs;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogDAO logDAO;

    public LogController() {
        this.logDAO = new LogDAO(); // Instantiate the DAO
    }

    // GET ALL OF EM
    @GetMapping
    public List<Logs> getAllLogs() {
        return logDAO.getAllLogs();
    }
}
