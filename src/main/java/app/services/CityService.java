package app.services;

import app.HibernateUtil;
import app.appDAO.*;
import app.appentities.City;
import app.appentities.Coordinates;
import app.appentities.Human;
import app.appentities.Logs;
import app.exceptions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.services.LoginService.extractUsername;

@Service
public class CityService {
    @Autowired
    private final CityDAO cityDAO;
    @Autowired
    private final HumanDAO humanDAO;

    @Autowired
    private static MinioService minioService;

    public CityService() {
        cityDAO = new CityDAO(); humanDAO = new HumanDAO(); // Instantiate the DAO
    }

    // this returns a list of all the cities
    public List<City> getAllCities() {
        return cityDAO.getAllCities();
    }

    // this returns a city by its ID
    public City getCityById(Long id) {
        System.out.println("tryna show the city rn");
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new CityNotFoundException(id);
        }
        return city;
    }

    // this adds a new city
    public City addCity(City city, String username) {

        if (CarCodeEnforcer.getArray(city.getCar_code().intValue())){
            throw new CarCodeException(city.getCar_code().intValue());
        }
        Coordinates coordinates = new Coordinates(
                city.getCoordinates().getX(),
                city.getCoordinates().getY()
        );
        CoordinatesDAO.saveCoordinates(coordinates);
        city.setCoordinates(coordinates);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            if (city.getGovernor() != null){
                if (city.getGovernor().getId() != null) {
                    Human governor = humanDAO
                            .getHumanById(city.getGovernor().getId());
                    city.setGovernor(governor);
                }
                else {
                    Human governor = new Human(
                            city.getGovernor().getName(),
                            city.getGovernor().getAge(),
                            city.getGovernor().getHeight(),
                            city.getCreated_by()
                    );
                    session.persist(governor);
                    city.setGovernor(governor);
                }
            }
            session.persist(city);
            Logs log = new Logs(
                    username, "ADDED", city.getName(), "", LocalDateTime.now()
            );
            LogDAO.saveLog(log);
            CarCodeEnforcer.setid(city.getCar_code().intValue(), true);
            transaction.commit();
        } catch (Exception e) {
            throw new CitySavingException(e);
        }
        return city;
    }

    // this updates an existing city by its ID
    public City updateCity(Long id, City updatedCity, String username) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new CityNotFoundException(id);
        }

        if (updatedCity.getName() != null) city.setName(updatedCity.getName());
        if (updatedCity.getArea() != null) city.setArea(updatedCity.getArea());
        if (updatedCity.getPopulation() != null) city.setPopulation(updatedCity.getPopulation());
        if (updatedCity.getEstablishment_date() != null) city.setEstablishment_date(updatedCity.getEstablishment_date());
        if (updatedCity.isCapital() != null) city.setCapital(updatedCity.isCapital());
        if (updatedCity.getMeters_above_sea_level() != null) city.setMeters_above_sea_level(updatedCity.getMeters_above_sea_level());
        if (updatedCity.getCar_code() != null) {
            if (CarCodeEnforcer.getArray(updatedCity.getCar_code().intValue())){
                throw new CarCodeException(updatedCity.getCar_code().intValue());
            }
            else
                city.setCar_code(updatedCity.getCar_code());
        }
        if (updatedCity.getClimate() != null) city.setClimate(updatedCity.getClimate());
        if (updatedCity.getStandardOfLiving() != null) city.setStandardOfLiving(updatedCity.getStandardOfLiving());
        if (updatedCity.getGovernor() != null) city.setGovernor(updatedCity.getGovernor());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.merge(city);
            Logs log = new Logs(
                    username, "UPDATED", city.getName(), "", LocalDateTime.now()
            );
            LogDAO.saveLog(log);
            CarCodeEnforcer.setid(city.getCar_code().intValue(), true);
            transaction.commit();
        } catch (Exception e) {
            throw new CitySavingException(e);
        }

        return city;
    }

    // this deletes a city by its ID
    public String deleteCity(Long id, String username) {
        City city = cityDAO.getCityById(id);
        if (city == null) {
            throw new CityNotFoundException(id);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(city);
            Logs log = new Logs(
                    username, "DELETED", city.getName(), "", LocalDateTime.now()
            );
            LogDAO.saveLog(log);
            CarCodeEnforcer.setid(city.getCar_code().intValue(),false);
            transaction.commit();
        } catch (Exception e) {
            throw new CityDeletionException(id, e);
        }

        return "City with id " + id + " has been deleted.";
    }

    // this retrieves the username of the person sending the request
    public String getUsername(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token from the header
            String token = authorizationHeader.substring(7);
            String username = extractUsername(token);

            return username;
        }
        else
            return "<unknown>";
    }

    // this returns average MASL
    public Double getavrgMASL() {
        return CityDAO.getavrgMASL();
    }

    // this returns a name list of cities with standard of living above
    public List<String> getCitiesWithSOL(@RequestParam("minSOL") String minSOL) {
        System.out.println("GOT " + minSOL);
        return CityDAO.getCitiesWithSOL(minSOL);
    }

    // this returns a name list of cities with Climate
    public List<String> getCitiesWithClimate() {
        return CityDAO.getCitiesWithClimate();
    }

    // this transfers city population from id1 to id2
    public void transfertoanother(@RequestParam("id1") int id1, @RequestParam("id2") int id2) {
        System.out.println("GOT " + id1 + " " + id2);
        CityDAO.transfertoanother(id1, id2);
    }

    // this transfers city population from id1 to the smallest city
    public void transfertosmallest(@RequestParam("id1") int id1) {
        System.out.println("GOT " + id1);
        CityDAO.transfertosmallest(id1);
    }


    // this adds a list of cities
    public ResponseEntity<Map<String, List <Long> >> massAddition(MultipartFile file, String username){
        // request to database and minio
        try {CityDAO.reloadCarCodes();}
        catch (Exception e){
            Map<String, List<Long> > response = new HashMap<>();
            throw new DatabaseConnectionException(e);
        }
        try {minioService.pingMinio();}
        catch (Exception e){
            Map<String, List<Long> > response = new HashMap<>();
            throw new MinioConnectionException(e);
        }
        // commit to both database and minio
        try {
            String fileName = file.getOriginalFilename();
            minioService.uploadFile(fileName, file.getInputStream(), file.getSize(), file.getContentType());
        }
        catch (Exception e){
            Map<String, List<Long> > response = new HashMap<>();
            throw new MinioDownException();
        }
        try {
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            yamlMapper.findAndRegisterModules();
            List<City> cities = yamlMapper.readValue(
                    file.getInputStream(),
                    yamlMapper.getTypeFactory().constructCollectionType(List.class, City.class)
            );
            List<Long> cityids = cityDAO.citiessaveALL(cities);
            Logs log = new Logs(
                    username, "MASS ADDED", "<multiple>", "", LocalDateTime.now()
            );
            LogDAO.saveLog(log);
            Map<String, List<Long>> response = new HashMap<>();
            response.put("ids", cityids);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            Map<String, List<Long> > response = new HashMap<>();
            throw new DatabaseDownException();
        }
    }
}
