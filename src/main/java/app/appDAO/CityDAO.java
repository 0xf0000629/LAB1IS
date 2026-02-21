package app.appDAO;

import app.appentities.City;
import app.appentities.Coordinates;
import app.appentities.Human;
import app.CacheMe;
import app.appentities.Users;
import app.exceptions.CarCodeException;
import app.exceptions.DatabaseDownException;
import app.exceptions.DatabaseException;
import jakarta.persistence.QueryHint;
import org.hibernate.annotations.QueryHints;
import org.hibernate.jpa.AvailableHints;
import org.hibernate.stat.Statistics;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.SessionFactory;
import app.HibernateUtil;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CityDAO {

    // this is the DAO for cities
    // does database transactions for cities and executes database functions
    @Autowired
    private SessionFactory sessionFactory;

    // this returns all of the cities
    @CacheMe
    @Transactional
    public List<City> getAllCities() {
        Transaction transaction = null;
        List<City> cities = null;

        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            cities = session.createQuery("from City", City.class).setHint(AvailableHints.HINT_CACHEABLE, true).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
        return cities;
    }

    // this returns a city by its ID
    @CacheMe
    @Transactional
    public City getCityById(Long id) {
        Transaction transaction = null;
        City city = null;

        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();
            city = session.get(City.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
        return city;
    }

    // this persists a city in the database
    @CacheMe
    @Transactional
    public void saveCity(City city) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.persist(city);
        transaction.commit();
        session.close();
    }

    // this calls the getaverageMASL function in the DB and returns the answer
    public static Double getavrgMASL(){
        Transaction transaction = null;
        Double avrg = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "SELECT getaverageMASL()";
            NativeQuery<Double> query = session.createNativeQuery(sql, Double.class);
            avrg = query.getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
        return avrg;
    }

    // this calls the getcitieswithSOL function in the DB and returns the answer
    public static List<String> getCitiesWithSOL(String minSOL){
        Transaction transaction = null;
        List <String> cities = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "SELECT getcitieswithSOL(:minSOL)";
            NativeQuery<String> query = session.createNativeQuery(sql, String.class);
            query.setParameter("minSOL", minSOL);

            cities = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
        return cities;
    }

    // this calls the getcitieswithClimate function in the DB and returns the answer
    public static List<String> getCitiesWithClimate(){
        Transaction transaction = null;
        List <String> cities = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "SELECT getcitieswithClimate()";
            cities = session.createQuery(hql, String.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }

        return cities;
    }

    // this gets all the city car codes from the database and reloads the list CarCodeEnforcer
    public static void reloadCarCodes(){
        Transaction transaction = null;
        List <Integer> carcodes = null;

        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            String hql = "SELECT car_code from city";
            carcodes = session.createNativeQuery(hql, Integer.class).list();
            System.out.println("Got car codes" + carcodes);
            for (int i=0;i<carcodes.size();i++){
                CarCodeEnforcer.setid(carcodes.get(i), true);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
        return;
    }

    // this calls the transfertoanother procedure from the DB
    public static void transfertoanother(int id1, int id2){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "CALL transfertoanother(:id1, :id2)";
            session.createNativeQuery(sql, String.class)
                    .setParameter("id1", id1)
                    .setParameter("id2", id2)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
    }

    // this calls the transfertosmallest procedure from the DB
    public static void transfertosmallest(int id1){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "CALL transfertosmallest(:id1)";
            session.createNativeQuery(sql, String.class)
                    .setParameter("id1", id1)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException(e);
        }
    }

    // this persists a list of cities
    @Transactional
    public List<Long> citiessaveALL(List<City> cities){
        List<Long> cityids = new ArrayList<>();
        Map<String, Human> govns = new HashMap<String,Human>();
        Session session = sessionFactory.getCurrentSession();
        Transaction ts = session.beginTransaction();
        try {
            for (int i = 0; i < cities.size(); i++) {
                City city = cities.get(i);
                if (CarCodeEnforcer.getArray(city.getCar_code().intValue())) {
                    for (int j = 0; j < i; j++) {
                        CarCodeEnforcer.setid(cities.get(j).getCar_code().intValue(), false);
                    }
                    throw new CarCodeException(city.getCar_code().intValue());
                }
                Coordinates coordinates = new Coordinates(
                        city.getCoordinates().getX(),
                        city.getCoordinates().getY()
                );
                session.persist(coordinates);
                city.setCoordinates(coordinates);

                Human governor = govns.get(city.getGovernor().getName());
                if (governor != null) {
                    city.setGovernor(governor);
                } else {
                    governor = new Human(
                            city.getGovernor().getName(),
                            city.getGovernor().getAge(),
                            city.getGovernor().getHeight(),
                            city.getCreated_by()
                    );
                    session.persist(governor);
                    govns.put(governor.getName(), governor);
                    city.setGovernor(governor);
                }

                session.persist(city);
                cityids.add(city.getId());
                CarCodeEnforcer.setid(city.getCar_code().intValue(), true);
            }
            ts.commit();
            return cityids;
        }
        catch (Exception e){
            if (ts != null) ts.rollback();
            throw new DatabaseException(e);
        }
    }

}