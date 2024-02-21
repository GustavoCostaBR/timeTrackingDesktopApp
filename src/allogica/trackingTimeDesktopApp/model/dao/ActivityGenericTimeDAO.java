package allogica.trackingTimeDesktopApp.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class ActivityGenericTimeDAO<T> extends GenericDAO<T> {

	public ActivityGenericTimeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void saveGenericActivityTime(T subactivity) {
		super.saveOrUpdate(subactivity);
	}
	
	public List<T> findByDateRange(Class<T> entityClass, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName() + " WHERE time >= :startDate AND time < :endDate ORDER BY time ASC", entityClass)
                          .setParameter("startDate", startDate.atStartOfDay())
                          .setParameter("endDate", endDate.atStartOfDay())
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}