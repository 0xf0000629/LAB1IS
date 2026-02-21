package app.services;

import app.HibernateUtil;
import app.SecurityUtil;
import app.appDAO.UserDAO;
import app.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import javax.security.auth.login.LoginException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import app.appentities.Users;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import app.SecurityUtil;

@RestController
@RequestMapping("/api/auth")
public class LoginService {

    private static final String SECRET_KEY = "thebigwitchdoyouthinkbigenderpeopleedgetheirpronouns";

    private final UserDAO userDAO;

    private final SecurityUtil secUtil;

    public LoginService(SecurityUtil secUtil) {
        this.userDAO = new UserDAO(); // Instantiate the DAO
        this.secUtil = secUtil;
    }

    // this returns a list of all users
    public List<Users> getAllUsers() {
        return userDAO.getAllUser();
    }

    // this returns a user by their username
    public ResponseEntity<Users> getUserByUsername(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String username = extractUsername(token);
            Users user = userDAO.findByUsername(username);

            if (user == null) {
                throw new UserNotFoundException();
            }

            return ResponseEntity.ok(user);
        }
        else{
            throw new UnauthorizedRequestException();
        }
    }

    // this returns a token if the user successfully logged in (password is same as in the DB)
    public ResponseEntity<Map<String, String>> login(Users user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String possiblepassword = userDAO.grabPassword(username, password);

        if (possiblepassword != null) {
            if (secUtil.confirmPassword(password, possiblepassword)) {
                // Generate a JWT token
                String authToken = generateJwtToken(username);

                // Prepare response
                Map<String, String> response = new HashMap<>();
                response.put("authToken", authToken);
                System.out.println(user.getUsername() + "logged in");

                return ResponseEntity.ok(response);
            }
        } else {
            throw new UserNotFoundException();
        }
        throw new LoginFailedException();
    }

    // this invalidates the user token
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
            throw new UnauthorizedRequestException();
        }
    }

    // this adds a new user to the database and returns a new token for them
    public ResponseEntity<Map<String, String>> register(Users user) {
        String username = user.getUsername();
        String password = user.getPassword();

        String hashedPassword = secUtil.hashPassword(password);

        Users newUser = new Users(
              username, hashedPassword, false
        );

        try {
            userDAO.saveUser(newUser);
        } catch (Exception e) {
            throw new RegistrationFailedException(e);
        }

        String authToken = generateJwtToken(username);

        Map<String, String> response = new HashMap<>();
        response.put("authToken", authToken);

        return ResponseEntity.ok(response);
    }

    // this tries promoting a user to admin by their ID
    public Users makeAdmin(Long id) {
        Users user = UserDAO.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException();
        }

        user.setCool(true);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            throw new PromotionException(id, e);
        }

        return user;
    }


    // this is the JWT token generator
    private String generateJwtToken(String username) {
        long expirationTime = 10000 * 60 * 60; // 1 hour in milliseconds
        SecretKey thekey = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(thekey)
                .compact();
    }

    // this extracts the username from the token
    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // this extracts a specific claim from the token
    public static <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // this extracts all claims from the token
    private static Claims extractAllClaims(String token) {
        SecretKey thekey = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
        return Jwts.parser()
                .verifyWith(thekey)
                .build().parseSignedClaims(token).getPayload();
    }
}
