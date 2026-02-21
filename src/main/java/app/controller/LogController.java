package app.controller;

import app.appDAO.LogDAO;
import app.appentities.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    private final LogDAO logDAO;

    public LogController() {
        this.logDAO = new LogDAO(); // Instantiate the DAO
    }

    // this catches requests for a list of logs
    @GetMapping
    public List<Logs> getAllLogs() {
        return logDAO.getAllLogs();
    }
}
