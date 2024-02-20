package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.dao.ActivityDAO;
import allogica.trackingTimeDesktopApp.model.dao.SubactivityEndDAO;
import allogica.trackingTimeDesktopApp.model.dao.SubactivityStartDAO;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.SubactivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.SubactivityStart;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityEndingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityStartingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoEndException;
import allogica.trackingTimeDesktoppApp.exceptions.UnconpatibleStartsEndsCount;


public class ActivityService {
	private ActivityDAO dao;
	private SubactivityEndDAO daoEnd;
	private SubactivityStartDAO daoStart;

	public ActivityService(SessionFactory sessionFactory) {
		dao = new ActivityDAO(sessionFactory);
		daoEnd = new SubactivityEndDAO(sessionFactory);
		daoStart = new SubactivityStartDAO(sessionFactory);
	}

	
	
	public void saveService(Activity activity) {
        dao.saveActivity(activity);
    }
	
	public void saveService(Activity activity, SubactivityStart subactivityStart) {
        dao.saveActivity(activity);
        daoStart.saveGenericSubactivityTime(subactivityStart);
    }
	
	public void saveService(Activity activity, SubactivityEnd subactivityEnd) {
        dao.saveActivity(activity);
        daoEnd.saveGenericSubactivityTime(subactivityEnd);
    }
	
	public void saveService(Activity activity, SubactivityStart subactivityStart, SubactivityEnd subactivityEnd) {
        dao.saveActivity(activity);
        daoStart.saveGenericSubactivityTime(subactivityStart);
        daoEnd.saveGenericSubactivityTime(subactivityEnd);
    }
	
	public void DeleteSubactivityStartService(Activity activity, SubactivityStart subactivityStart) {
		activity.deleteSubActivityStart(subactivityStart);
		daoStart.delete(subactivityStart);
		saveService(activity);
	}
	
	
	
	public Activity calcEnd(Activity activity) throws ActivityEndingTimeException, UnconpatibleStartsEndsCount {
		LocalDateTime end;
		try {
			end = activity.getLastEnd().getTime();
		} catch (ThereIsNoEndException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			end = null;
		}
		if (end != null && (activity.getActivityStartCount() == activity.getActivityEndCount())) {
			return activity;
		}
//		Starting verification of subactivities ends if it is not complete in the activity itself
		Map<Long, Activity> subactivities = activity.getSubactivities();
		if (!(subactivities.isEmpty())){
			end = LocalDateTime.of(1900, 1, 1, 0, 0);
			for (Activity subactivity : subactivities.values()) {
				LocalDateTime subEnd;
				try {
					subEnd = subactivity.getLastEnd().getTime();
				} catch (ThereIsNoEndException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					subEnd = null;
				}
				if (subEnd != null) {
					if (subEnd.isAfter(end)) {
						end = subEnd;
					}
				} else {
					try {
						subEnd = this.calcEnd(subactivity).getLastEnd().getTime();
					} catch (ThereIsNoEndException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						subEnd = null;
					} 
					if (subEnd != null && subEnd.isAfter(end)) {
						end = subEnd;
					}
				}
			}
			if (activity.getActivityStartCount() == (activity.getActivityEndCount() + 1)) {
				activity.addEnd(end);
				try {
					saveService(activity, activity.getLastEnd());
				} catch (ThereIsNoEndException e) {
					e.printStackTrace();
				} return activity;
			}
			else if (activity.getActivityStartCount() == activity.getActivityEndCount()) {
				try {
					activity.deleteSubActivityEnd(activity.getLastEnd());
					activity.addEnd(end);
					saveService(activity, activity.getLastEnd());
					return activity;
				} catch (ThereIsNoEndException e) {
					System.out.println("Not suposed to happen! The activity Id is: " + activity.getId());
					e.printStackTrace();
					return null;
				} 
			}
			else {
				throw new UnconpatibleStartsEndsCount("The number of ends and starts should be simillar! The activity Id is: " + activity.getId() + ". And the activity name is: "  + activity.getName()+ ".");
			}
		}
		else {
			throw new ActivityEndingTimeException("Or the activity should have an ending time for each starting time or it should have subactivities. The activity Id is: " + activity.getId() + ".");
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
