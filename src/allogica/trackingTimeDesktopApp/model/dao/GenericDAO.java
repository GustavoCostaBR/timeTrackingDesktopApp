package allogica.trackingTimeDesktopApp.model.dao;

import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
    
    
    
    public List<T> findByProperty(Class<T> entityClass, String propertyName, Object propertyValue) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE e." + propertyName + " = :propertyValue";
            Query<T> query = session.createQuery(hql, entityClass);
            query.setParameter("propertyValue", propertyValue);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void findByPropertyAndUpdateOther(Class<T> entityClass, String propertyName, Object propertyValue, String propertyToUpdate, Object newValue) {
        try (Session session = sessionFactory.openSession()) {
            // Find entities based on the specified property
            List<T> entities = findByProperty(entityClass, propertyName, propertyValue);

            // Update the specified property for each retrieved entity
            if (entities != null) {
                Transaction transaction = session.beginTransaction();
                for (T entity : entities) {
                    // Update the specified property
                    try {
                        Method setterMethod = entityClass.getMethod(getSetterMethodName(propertyToUpdate), newValue.getClass());
                        setterMethod.invoke(entity, newValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    session.update(entity);
                }
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    public void findAndUpdate(Class<T> entityClass, String propertyName, Object propertyValue, Object newValue) {
        try (Session session = sessionFactory.openSession()) {
            // Find entities based on the specified property
            List<T> entities = findByProperty(entityClass, propertyName, propertyValue);

            // Update the specified property for each retrieved entity
            if (entities != null) {
                Transaction transaction = session.beginTransaction();
                for (T entity : entities) {
                    // Update the specified property directly
                    updateProperty(entity, propertyName, newValue);
                    session.update(entity);
                }
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    
    // Utility method to update a specific property of an entity
    private void updateProperty(T entity, String propertyName, Object newValue) {
        try {
            // Call the appropriate setter method based on the property name
            Method setterMethod = entity.getClass().getMethod(getSetterMethodName(propertyName), newValue.getClass());
            setterMethod.invoke(entity, newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
 // Utility method to construct setter method name
    private String getSetterMethodName(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
    }
    
    
    
    
}


