package app.controller;

import app.HibernateUtil;
import app.appDAO.CityDAO;
import app.appDAO.UserDAO;
import app.appentities.City;
import app.appentities.Human;
import app.services.LoginService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import app.appentities.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private static final String SECRET_KEY = "thebigwitchdoyouthinkbigenderpeopleedgetheirpronouns"; // Replace with a secure key

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService; // Instantiate the DAO
    }

    // this catches requests for a list of all registered users
    @GetMapping
    public List<Users> getAllUsersRequest() {
        return loginService.getAllUsers();
    }

    // this catches requests for the username of the logged in user
    @GetMapping("/me")
    public ResponseEntity<Users> getUserByUsernameRequest(HttpServletRequest request) {
        return loginService.getUserByUsername(request);
    }

    // this catches requests for logging in
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginRequest(@RequestBody Users user) {
        return loginService.login(user);
    }

    // this catches requests for logging out
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutRequest(HttpServletRequest request) {
        return loginService.logout(request);
    }

    // this catches requests for registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerRequest(@RequestBody Users user) {
        return loginService.register(user);
    }

    // this catches requests for make someone an admin
    @PutMapping("/{id}")
    public Users makeAdminRequest(@PathVariable("id") Long id) {
        return loginService.makeAdmin(id);
    }
}
