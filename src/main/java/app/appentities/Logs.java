package app.appentities;

import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(
        usage = CacheConcurrencyStrategy.READ_WRITE,
        region = "LogsRegion"
)
public class Logs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;
    private String nameofentity;
    private String filename;
    private LocalDateTime log_date;

    public Logs() {
        // this empty constructor is for JPA to not break
    }
    public Logs(String username, String action, String nameofentity, String filename, LocalDateTime log_date){
        this.username = username;
        this.action = action;
        this.nameofentity = nameofentity;
        this.filename = filename;
        this.log_date = log_date;
    }

    public LocalDateTime getLog_date() {
        return log_date;
    }

    public void setLog_date(LocalDateTime log_date) {
        this.log_date = log_date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNameofentity() {
        return nameofentity;
    }

    public void setNameofentity(String nameofentity) {
        this.nameofentity = nameofentity;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
