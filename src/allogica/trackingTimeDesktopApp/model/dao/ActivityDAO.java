package allogica.trackingTimeDesktopApp.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import allogica.trackingTimeDesktopApp.model.entity.Activity;

public class ActivityDAO extends GenericDAO<Activity> {

	public ActivityDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public Activity getActivityById(Long id) {
		return super.findById(Activity.class, id);
	}

	public Map<Long, Activity> getFirstLevelSubactivities(Long activityId) {
		try (Session session = sessionFactory.openSession()) {
			String hql = "SELECT a FROM Activity a WHERE a.parentActivityId = :parentId";
            Query<Activity> query = session.createQuery(hql, Activity.class);
            query.setParameter("parentId", activityId);
            List<Activity> subactivities = query.getResultList();
			Map<Long, Activity> subactivitiesMap = new HashMap<>();
			for (Activity subactivity : subactivities) {
				subactivitiesMap.put(subactivity.getId(), subactivity);
			}
			return subactivitiesMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<Long, Activity> getAllSubactivities(Long activityId) {
		Map<Long, Activity> subactivities = getFirstLevelSubactivities(activityId);
		for (Map.Entry<Long, Activity> subactivity : subactivities.entrySet()) {
			subactivity.getValue().setSubActivities(getAllSubactivities(subactivity.getKey()));
		}
		return subactivities;
	}

	public void delete(Long activityId, Boolean deleteSubActivities) {
		Transaction transaction = null;
		try (Session session = sessionFactory.openSession()) {
			transaction = session.beginTransaction();
			Map<Long, Activity> allSubactivities = this.getAllSubactivities(activityId);
			if (!(allSubactivities.isEmpty()) && deleteSubActivities) {
				for (Map.Entry<Long, Activity> subactivity : allSubactivities.entrySet()) {
					delete(subactivity.getKey(), true);
				}
				super.delete(getActivityById(activityId));
			} else {
				if ((allSubactivities.isEmpty())) {
					super.delete(this.getActivityById(activityId));
				} else {
					Map<Long, Activity> firstLevelSubActivities = getFirstLevelSubactivities(activityId);
					for (Map.Entry<Long, Activity> subactivity : firstLevelSubActivities.entrySet()) {
						changeParentActivityId(subactivity.getKey(), 0L);
				}
			}
		}
		transaction.commit();
	}catch(

	Exception e)
	{
		if (transaction != null) {
			transaction.rollback();
		}
		e.printStackTrace();
	}
	}

	public void changeParentActivityId(Long activityId, Long newParentActivityId) {
		super.findByPropertyAndUpdateOther(Activity.class, "parentActivityId", activityId, "parentActivityId", newParentActivityId);
				
//		Transaction transaction = null;
//		try (Session session = sessionFactory.openSession()) {
//			transaction = session.beginTransaction();
//
//			// Retrieve the Activity object by its ID
//			Activity activity = session.get(Activity.class, activityId);
//
//			if (activity != null) {
//				// Update the parent_activity_id attribute
//				activity.setParentActivityId(newParentActivityId);
//
//				// Save or update the Activity object in the database
//				session.saveOrUpdate(activity);
//			}
//
//			transaction.commit();
//		} catch (Exception e) {
//			if (transaction != null) {
//				transaction.rollback();
//			}
//			e.printStackTrace();
//		}
	}

}
