package app.appDAO;

import app.appentities.City;
import app.appentities.Users;
import org.hibernate.SessionFactory;
import app.HibernateUtil;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Repository
public class CityDAO {

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

    public void saveCity(City city) {
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

    public static void transfertoanother(int id1, int id2){
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String hql = "SELECT SELECT transfertoanother("+ String.valueOf(id1) + ", " + String.valueOf(id2) + ")";
            session.createQuery(hql, String.class).list();
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
            String hql = "SELECT SELECT transfertosmallest("+ String.valueOf(id1) + ")";
            session.createQuery(hql, String.class).list();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) transaction.rollback();
        }
    }

}