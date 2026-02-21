package app;

import app.appDAO.UserDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class SecurityUtil {

    private final BCryptPasswordEncoder passwordEncoder;
    private final String pepper;

    // this instantiates the password encoder and grabs pepper from environment vars
    public SecurityUtil(@Value("${CITY_APP_PEPPER}") String pepper) {
        this.passwordEncoder = new BCryptPasswordEncoder(12);
        this.pepper = pepper;
        System.out.println("I grabbed the pepper and it's " + pepper);
    }

    // this tests if raw and hashed passwords are the same, used for login
    public Boolean confirmPassword(String raw, String hashed){
        return this.passwordEncoder.matches(raw+pepper, hashed);
    }

    // this hashes the password and returns what it got
    public String hashPassword(String rawPassword) {
        String peppered = rawPassword + pepper;
        return passwordEncoder.encode(peppered);
    }
}
