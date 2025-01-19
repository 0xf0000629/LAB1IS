package app.appDAO;

import app.controller.CityController;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CarCodeEnforcer {

    // Use a thread-safe list for concurrency
    private static List<Boolean> carcodes = new CopyOnWriteArrayList<Boolean>(Collections.nCopies(1000, false));

    @PostConstruct
    public static void runitback(){
        CityDAO.reloadCarCodes();
        System.out.println("CAR CODES RELOADED");
    }

    public static Boolean getArray(int id) {
        return carcodes.get(id);
    }

    public static void setid(int id, boolean state) {
        carcodes.set(id, state);
    }
}