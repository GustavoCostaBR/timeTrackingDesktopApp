package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.dao.ActivityDAO;
import allogica.trackingTimeDesktopApp.model.entity.Activity;

public class ActivityService{
	private ActivityDAO dao;
	
	public ActivityService(SessionFactory sessionFactory) {
		dao = new ActivityDAO(sessionFactory);
	}
	
	public Activity totalTime(Activity activity) {
		activity.calcTotalTime();
	}
	
	
	public Activity calcTotalTime(Activity activity) {
		Duration totalTime = Duration.ZERO;
		LocalDateTime start = activity.getStart();
		LocalDateTime end = activity.getEnd();
		
		if ((start != null && end != null) && activity.getSubactivities().isEmpty()) {
			return totalTime = Duration.between(start, end);
		} else {
			for (Activity subactivity : subactivities.values()) {
				totalTime = totalTime.plus(subactivity.calcTotalTime());
			}
			return totalTime;
		}
	}
	
	
	
}
