package allogica.trackingTimeDesktopApp.model.dao;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

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
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}

	public void saveActivity(Activity activity) {
		super.saveOrUpdate(activity);
	}
	
	public void changeParentActivityId(Long parentActivityId, Long newParentActivityId) {
		super.findAndUpdate(Activity.class, "parentActivityId", parentActivityId, newParentActivityId);
	}
	
	
	
	public void changeDescription(Long activityId, String description) {
		Activity activity = super.findById(Activity.class, activityId);
		activity.setDescription(description);
		saveActivity(activity);
	}
	public void changeAddStart(Long activityId, LocalDateTime start) {
		Activity activity = super.findById(Activity.class, activityId);
		ActivityStart subactivityStart = new ActivityStart(activity, start);
		ActivityStartDAO subactivityStartDAO = new ActivityStartDAO(sessionFactory);
	    subactivityStartDAO.saveGenericActivityTime(subactivityStart);
		activity.addEnd(start);		
		saveActivity(activity);
	}
	public void changeAddEnd(Long activityId, LocalDateTime end) {
		Activity activity = super.findById(Activity.class, activityId);
		ActivityEnd subactivityEnd = new ActivityEnd(activity, end);
		ActivityEndDAO subactivityEndDAO = new ActivityEndDAO(sessionFactory);
	    subactivityEndDAO.saveGenericActivityTime(subactivityEnd);
		activity.addEnd(end);		
		saveActivity(activity);
	}
	public void changeName(Long activityId, String name) {
		Activity activity = super.findById(Activity.class, activityId);
		activity.setName(name);
		saveActivity(activity);
	}
	public void changeTotalTime(Long activityId, Duration tempo) {
		Activity activity = super.findById(Activity.class, activityId);
		activity.setTotalTime(tempo);
		saveActivity(activity);
	}
	
}
