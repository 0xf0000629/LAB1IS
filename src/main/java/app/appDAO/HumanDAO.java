package app.appDAO;


import app.HibernateUtil;
import app.appentities.Human;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Repository
public class HumanDAO {

    public void saveHuman(Human human) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(human);
        transaction.commit();
        session.close();
    }
}
