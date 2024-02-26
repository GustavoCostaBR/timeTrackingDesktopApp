package allogica.trackingTimeDesktopApp.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import allogica.trackingTimeDesktopApp.model.entity.ActivityTime;
import jakarta.transaction.Transactional;

//public abstract class ActivityGenericTimeRepository<T> extends GenericDAO<T> {

	// In a common abstract repository superclass:
@Transactional // Optimize for read-only operations
public interface ActivityGenericTimeRepository<T extends ActivityTime> extends JpaRepository<T, Long> {
    List<T> findByTimeBetweenOrderByTimeAsc(LocalDateTime startDate, LocalDateTime endDate);
    
    
//	public ActivityGenericTimeRepository(SessionFactory sessionFactory) {
//		super(sessionFactory);
//	}

//	public void saveGenericActivityTime(T subactivity) {
//		super.saveOrUpdate(subactivity);
//	}
	
//	public List<T> findByDateRange(Class<T> entityClass, LocalDate startDate, LocalDate endDate) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM " + entityClass.getSimpleName() + " WHERE time >= :startDate AND time < :endDate ORDER BY time ASC", entityClass)
//                          .setParameter("startDate", startDate.atStartOfDay())
//                          .setParameter("endDate", endDate.atStartOfDay())
//                          .list();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
	
}