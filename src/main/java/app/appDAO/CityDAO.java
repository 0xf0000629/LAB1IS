package app.appDAO;

import app.appentities.City;
import app.appentities.Coordinates;
import app.appentities.Human;
import app.appentities.Users;
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

    @Autowired
    private SessionFactory sessionFactory;

    public List<City> getAllCities() {
        Transaction transaction = null;
        List<City> cities = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            cities = session.createQuery("from City", City.class).list();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return cities;
    }

    public City getCityById(Long id) {
        Transaction transaction = null;
        City city = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            city = session.get(City.class, id);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return city;
    }

    public static void saveCity(City city) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(city);
        transaction.commit();
        session.close();
    }

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
            e.printStackTrace();
        }
        return avrg;
    }

    public static List<String> getCitiesWithSOL(String minSOL){
        Transaction transaction = null;
        List <String> cities = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "SELECT getcitieswithSOL('" + minSOL + "')";
            NativeQuery<String> query = session.createNativeQuery(sql);
            cities = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
        return cities;
    }

    public static List<String> getCitiesWithClimate(){
        Transaction transaction = null;
        List <String> cities = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "SELECT getcitieswithClimate()";
            cities = session.createQuery(hql, String.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }

        return cities;
    }

    public static void reloadCarCodes(){
        Transaction transaction = null;
        List <Integer> carcodes = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "SELECT car_code from city";
            carcodes = session.createNativeQuery(hql, Integer.class).list();
            System.out.println("Got car codes" + carcodes);
            for (int i=0;i<carcodes.size();i++){
                CarCodeEnforcer.setid(carcodes.get(i), true);
            }
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }
        return;
    }

    public static void transfertoanother(int id1, int id2){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "CALL transfertoanother(:id1, :id2)";
            session.createNativeQuery(sql)
                    .setParameter("id1", id1)
                    .setParameter("id2", id2)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }
    }

    public static void transfertosmallest(int id1){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "CALL transfertosmallest(:id1)";
            session.createNativeQuery(sql)
                    .setParameter("id1", id1)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }
    }

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
                    throw new RuntimeException("this car code is already in use.");
                }
                Coordinates coordinates = new Coordinates();
                coordinates.setX(city.getCoordinates().getX());
                coordinates.setY(city.getCoordinates().getY());
                session.save(coordinates);
                city.setCoordinates(coordinates);

                Human governor = govns.get(city.getGovernor().getName());
                if (governor != null) {
                    city.setGovernor(governor);
                } else {
                    governor = new Human();
                    governor.setName(city.getGovernor().getName());
                    governor.setAge(city.getGovernor().getAge());
                    governor.setHeight(city.getGovernor().getHeight());
                    session.save(governor);
                    govns.put(governor.getName(), governor);
                    city.setGovernor(governor);
                }

                session.save(city);
                cityids.add(city.getId());
                CarCodeEnforcer.setid(city.getCar_code().intValue(), true);
            }
            ts.commit();
            return cityids;
        }
        catch (Exception e){
            if (ts != null) ts.rollback();
            throw new RuntimeException("Error adding cities: " + e.getMessage());
        }
    }

}