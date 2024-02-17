package allogica.trackingTimeDesktopApp.model.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.Activity;


public class ActivityDAO extends GenericDAO<Activity> {
	
	public ActivityDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
	
	
	public List<Activity> getAllSubactivities(Long activityId) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Activity> query = builder.createQuery(Activity.class);
            Root<Activity> root = query.from(Activity.class);
            query.select(root)
                 .where(builder.equal(root.get("parent_activity_id"), activityId));
            List<Activity> subactivities = session.createQuery(query).getResultList();
            // Recursively fetch sub-subactivities if any
            for (Activity subactivity : subactivities) {
                subactivity.setSubActivities(getAllSubactivities(subactivity.getId()));
            }
            return subactivities;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	
	
}
