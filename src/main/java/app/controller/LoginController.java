package app.controller;

import app.HibernateUtil;
import app.appDAO.CityDAO;
import app.appDAO.UserDAO;
import app.appentities.City;
import app.appentities.Human;
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

import static app.SecurityUtil.hashPassword;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private static final String SECRET_KEY = "thebigwitchdoyouthinkbigenderpeopleedgetheirpronouns"; // Replace with a secure key

    private final UserDAO userDAO;

    public LoginController() {
        this.userDAO = new UserDAO(); // Instantiate the DAO
    }

    @GetMapping
    public List<Users> getAllUsers() {
        return userDAO.getAllUser();
    }

    @GetMapping("/me")
    public ResponseEntity<Users> getUserByUsername(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token from the header
            String token = authorizationHeader.substring(7);
            String username = extractUsername(token);
            Users user = userDAO.findByUsername(username);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "who the hell are you");
            }

            return ResponseEntity.ok(user);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request issues");
        }
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

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token from the header
            String token = authorizationHeader.substring(7);
            String username = extractUsername(token);
            System.out.println(username + " logged out");

            Map<String, String> response = new HashMap<>();
            return ResponseEntity.ok(response);
        }
        else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request issues");
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

    @PutMapping("/{id}")
    public Users makeAdmin(@PathVariable("id") Long id) {
        Users user = UserDAO.getUserById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        user.setCool(true);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error promoting: " + e.getMessage());
        }

        return user;
    }

    private String generateJwtToken(String username) {
        long expirationTime = 10000 * 60 * 60; // 1 hour in milliseconds
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public static <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build().parseSignedClaims(token).getPayload();
    }
}
