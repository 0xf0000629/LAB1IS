package app.controller;

import app.appDAO.CityDAO;
import app.appDAO.UserDAO;
import app.appentities.City;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import app.appentities.Users;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.SecurityUtil.hashPassword;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private static final String SECRET_KEY = "killingmyselfpostponed"; // Replace with a secure key

    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO(); // Instantiate the DAO
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return userDAO.getAllUser();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (userDAO.confirm(username, password)) {
            // Generate a JWT token
            String authToken = generateJwtToken(username);

            // Prepare response
            Map<String, String> response = new HashMap<>();
            response.put("authToken", authToken);

            return ResponseEntity.ok(response);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Users user) {
        String username = user.getUsername();
        String password = user.getPassword();

        // Hash the password
        String hashedPassword = hashPassword(password);

        // Create and save the user
        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setPassword(hashedPassword);

        try {
            userDAO.saveUser(newUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error registering user", e);
        }

        // Generate a JWT token
        String authToken = generateJwtToken(username);

        // Prepare response
        Map<String, String> response = new HashMap<>();
        response.put("authToken", authToken);

        return ResponseEntity.ok(response);
    }

    private String generateJwtToken(String username) {
        long expirationTime = 1000 * 60 * 60; // 1 hour in milliseconds
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
