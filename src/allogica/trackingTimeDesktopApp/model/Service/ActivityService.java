package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.dao.ActivityDAO;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.SubactivityStart;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityEndingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityStartingTimeException;


public class ActivityService {
	private ActivityDAO dao;

	public ActivityService(SessionFactory sessionFactory) {
		dao = new ActivityDAO(sessionFactory);
	}

	
	
	public void save(Activity activity) {
        dao.saveActivity(activity);
    }
	
	public void DeleteSubactivityStartService(Activity activity, SubactivityStart subactivityStart) {
		activity.deleteSubActivityStart(subactivityStart);
		dao.delete(activity);
	}
	
	
	
	public Activity calcEnd(Activity activity) throws ActivityEndingTimeException {
		LocalDateTime end = activity.getEnd();
		Map<Long, Activity> subactivities = activity.getSubactivities();
		if (end != null) {
			save(activity);
			return activity;
		}
		if (!(subactivities.isEmpty())){
			end = LocalDateTime.of(1900, 1, 1, 0, 0);
			for (Activity subactivity : subactivities.values()) {
				LocalDateTime subEnd = subactivity.getEnd();
				if (subEnd != null) {
					if (subEnd.isAfter(end)) {
						end = subEnd;
					}
				} else {
					subEnd = this.calcEnd(subactivity).getEnd();
					if (subEnd.isAfter(end)) {
						end = subEnd;
					}
				}
			}
			activity.setEnd(end);
			save(activity);
			return activity;
		}
		else {
			throw new ActivityEndingTimeException("Or the activity should have an ending time or it should have subactivities. The activity Id id: " + activity.getId() + ".");
		}
		
	}
	
	
	
	public Activity calcTotalTime(Activity activity) throws ActivityEndingTimeException, ActivityStartingTimeException {
		Duration totalTime = Duration.ZERO;
		LocalDateTime start = activity.getStart();
		LocalDateTime end = activity.getEnd();
		
		if (start != null && end != null) {
			totalTime = Duration.between(start, end);
			activity.setTotalTime(totalTime);
			save(activity);
			return activity;

		} else if (start != null) {
			end = this.calcEnd(activity).getEnd();
			totalTime = Duration.between(start, end);
			activity.setTotalTime(totalTime);
			save(activity);
			return activity;
		}
		else {
			throw new ActivityStartingTimeException("The activity with activityID equals to " + activity.getId() + " has no starting time.");
		}
	}
	
	
	
	public Activity calcUsefulTime(Activity activity) {
		
		return activity;
	}

}
//
//} catch (ActivityEndingTimeException e) {
//	throw new ActivityEndingTimeException(e.getMessage());
