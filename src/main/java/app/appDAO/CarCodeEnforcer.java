package app.appDAO;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CarCodeEnforcer {

    // CarCodeEnforcer is responsible for the car codes
    // this is a list of car codes
    private static List<Boolean> carcodes = new CopyOnWriteArrayList<Boolean>(Collections.nCopies(1000, false));

    // this calls CityDAO to reload the car code list for future checks
    @PostConstruct
    public static void callForCodeReload(){
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