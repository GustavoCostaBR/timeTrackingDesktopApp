package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public abstract class GenericDAO<T> {

    protected final SessionFactory sessionFactory;

    public GenericDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveOrUpdate(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public T findById(Class<T> clazz, Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(clazz, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<T> findAll(Class<T> clazz) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + clazz.getSimpleName(), clazz).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


