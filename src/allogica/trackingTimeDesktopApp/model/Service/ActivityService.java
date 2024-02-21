package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.dao.ActivityDAO;
import allogica.trackingTimeDesktopApp.model.dao.ActivityEndDAO;
import allogica.trackingTimeDesktopApp.model.dao.ActivityStartDAO;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;
import allogica.trackingTimeDesktopApp.model.entity.ActivityTime;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityEndingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityStartingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.IncompatibleStartsEndsCount;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoEndException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoStartException;

public class ActivityService {
	private ActivityDAO dao;
	private ActivityEndDAO daoEnd;
	private ActivityStartDAO daoStart;

	public ActivityService(SessionFactory sessionFactory) {
		dao = new ActivityDAO(sessionFactory);
		daoEnd = new ActivityEndDAO(sessionFactory);
		daoStart = new ActivityStartDAO(sessionFactory);
	}

	public void saveService(Activity activity) {
		dao.saveActivity(activity);
	}

	public void saveService(Activity activity, ActivityStart subactivityStart) {
		dao.saveActivity(activity);
		daoStart.saveGenericActivityTime(subactivityStart);
	}

	public void saveService(Activity activity, ActivityEnd subactivityEnd) {
		dao.saveActivity(activity);
		daoEnd.saveGenericActivityTime(subactivityEnd);
	}

	public void saveService(Activity activity, ActivityStart subactivityStart, ActivityEnd subactivityEnd) {
		dao.saveActivity(activity);
		daoStart.saveGenericActivityTime(subactivityStart);
		daoEnd.saveGenericActivityTime(subactivityEnd);
	}

	
	public Activity stopsCurrentActivityService(Boolean state1) {
		Activity activity = dao.stopsCurrentActivity(state1);
		try {
			saveService(activity, activity.getLastEnd());
		} catch (ThereIsNoEndException e) {
			System.out.println("Serious. It's not supposed to happen! The problem happend in stopsCurrentActivityService");
			e.printStackTrace();
		}
		return activity;
	}
	
	public void checkIntervalAvailability(LocalDate dayInput) {
		List <ActivityEnd> ends = daoEnd.findByDateRange(ActivityEnd.class, dayInput, dayInput.plusDays(1));
		List <ActivityStart> starts = daoStart.findByDateRange(ActivityStart.class, dayInput, dayInput.plusDays(1));
//		if (ends.get(0).getActivity().getId())
		List <LocalDateTime> endsTime = new ArrayList<LocalDateTime>();
		List <LocalDateTime> startsTime = new ArrayList<LocalDateTime>();
		for (ActivityEnd end : ends) {
			endsTime.add(end.getTime());
		}
		for (ActivityStart start : starts) {
			startsTime.add(start.getTime());
		}
		
	}
	
	public void deleteActivityStartService(Activity activity, ActivityStart activityStart) {
		activity.deleteActivityStart(activityStart);
		daoStart.delete(activityStart);
		saveService(activity);
	}
	
	public Activity addActivityStartService(Activity activity, ActivityTime activityTimeStart) throws ThereIsNoEndException, ThereIsNoStartException {
		Activity currentActivity = dao.findByProperty(Activity.class, "current", true).get(0);
		if (currentActivity.getActivityEndCount() == currentActivity.getActivityStartCount()) {
			if (activityTimeStart.getTime().isAfter(currentActivity.getLastEnd().getTime())) {
				stopsCurrentActivityService(false);
				activity.addStart(activityTimeStart);
				activity.setCurrent(true);
				saveService(activity, (ActivityStart)activityTimeStart);
				return activity;
			}
			else if (activityTimeStart.getTime().isAfter(currentActivity.getLastStart().getTime()))  {
				deleteActivityEndService(currentActivity, currentActivity.getLastEnd());
				addActivityEndService(currentActivity, (ActivityEnd)activityTimeStart);
				stopsCurrentActivityService(false);
				activity.addStart(activityTimeStart);
				activity.setCurrent(true);
				saveService(activity, (ActivityStart)activityTimeStart);
				return activity;
		}	
		}
		return activity;
//		if ()
//		super.findByProperty(Activity.class, "current", true);
//		activity.addStart(activityStart);
//		saveService(activity, activityStart);
//		return activity;
	}	
	public Activity addActivityStartService(Activity activity) {
//		If the is use, I will get the activity that got stopped
		ActivityStart newStart;
		try {
			newStart = new ActivityStart(activity, stopsCurrentActivityService(false).getLastEnd().getTime()); 
			activity.addStart(newStart);
			activity.setCurrent(true);
			saveService(activity, newStart);
			return activity;
		} catch (ThereIsNoEndException e) {
			System.out.println("Serious. It's not supposed to happen! The problem happend in the addActivityStartService");
			e.printStackTrace();
		}
		return activity;
	}
	
	public void deleteActivityEndService(Activity activity, ActivityEnd activityEnd) {
		activity.deleteActivityEnd(activityEnd);
		daoEnd.delete(activityEnd);
		saveService(activity);
	}
	
	public Activity addActivityEndService(Activity activity, ActivityEnd activityEnd) {
		activity.addEnd(activityEnd);
		saveService(activity, activityEnd);
		return activity;
	}
	
	public Activity addActivityEndService(Activity activity) {
		ActivityEnd newEnd = new ActivityEnd(activity, LocalDateTime.now());
		activity.addEnd(newEnd);
		saveService(activity, newEnd);
		return activity;
	}

	public Activity calcEnd(Activity activity) throws ActivityEndingTimeException, IncompatibleStartsEndsCount {
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
		if (!(subactivities.isEmpty())) {
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
				}
				return activity;
			} else if (activity.getActivityStartCount() == activity.getActivityEndCount()) {
				try {
					activity.deleteActivityEnd(activity.getLastEnd());
					activity.addEnd(end);
					saveService(activity, activity.getLastEnd());
					return activity;
				} catch (ThereIsNoEndException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				throw new IncompatibleStartsEndsCount(
						"The number of ends and starts should be simillar! The activity Id is: " + activity.getId()
								+ ". And the activity name is: " + activity.getName() + ".");
			}
		} else {
			throw new ActivityEndingTimeException(
					"Or the activity should have an ending time for each starting time or it should have subactivities. The activity Id is: "
							+ activity.getId() + ".");
		}

	}

	public Activity calcTotalTime(Activity activity) throws ActivityEndingTimeException, ActivityStartingTimeException,
			IncompatibleStartsEndsCount, ThereIsNoStartException, ThereIsNoEndException {
		Duration totalTime = Duration.ZERO;
		LocalDateTime start;
		LocalDateTime end;
		if (activity.getActivityStartCount() != 0) {
			if (activity.getActivityStartCount() == activity.getActivityEndCount()) {
				start = activity.getFirstStart().getTime();
				end = activity.getLastEnd().getTime();
				totalTime = Duration.between(start, end);
				if (activity.getTotalTime() != totalTime) {
					activity.setTotalTime(totalTime);
					saveService(activity);
					return activity;
				}
				return activity;
			}
			if (activity.getActivityStartCount() == (activity.getActivityEndCount() + 1)) {
				Activity temporary;
				try {
					temporary = calcEnd(activity);
					activity = temporary;
				} catch (ActivityEndingTimeException e) {
					e.printStackTrace();
					start = activity.getFirstStart().getTime();
					totalTime = Duration.between(start, LocalDateTime.now());
					activity.setTotalTime(totalTime);
					saveService(activity);
					return activity;
				} catch (IncompatibleStartsEndsCount e) {
					System.out.println("Serious. It's not supposed to happen!");
					e.printStackTrace();
					start = activity.getFirstStart().getTime();
					totalTime = Duration.between(start, LocalDateTime.now());
					activity.setTotalTime(totalTime);
					saveService(activity);
					return activity;
				}
				start = activity.getFirstStart().getTime();
				end = activity.getLastEnd().getTime();
				totalTime = Duration.between(start, LocalDateTime.now());
				if (totalTime != activity.getTotalTime()) {
					activity.setTotalTime(totalTime);
					saveService(activity);
					return activity;
				}
			} else {
				throw new IncompatibleStartsEndsCount(
						"The number of ends and starts should be simillar! The activity Id is: " + activity.getId()
								+ ". And the activity name is: " + activity.getName() + ".");
			}
		} else {
			throw new ActivityStartingTimeException(
					"The activity with activityID equals to " + activity.getId() + " has no starting time.");
		}
//		It will never get to this point, but for the eclipse IDE to be happy...
		return activity;
	}

	public Activity calcUsefulTime(Activity activity) throws ActivityEndingTimeException, IncompatibleStartsEndsCount {
		Map<Long, Activity> subActivities = activity.getSubactivities();
		List<LocalDateTime> ends = activity.getEndTime();
		List<LocalDateTime> starts = activity.getStartTime();
		if (subActivities.isEmpty()) {
			if (starts.size() == ends.size() + 1) {
				try {
					activity = calcEnd(activity);
					ends = activity.getEndTime();
					if (starts.size() == ends.size()) {
						activity.sumUsefulTime(starts, ends);
						saveService(activity);
					}
				} catch (ActivityEndingTimeException e) {
					e.printStackTrace();
					System.out.println("It is going to calculate considering the time from now!");
					ends.add(LocalDateTime.now());
					activity.sumUsefulTime(starts, ends);
					saveService(activity);
				} catch (IncompatibleStartsEndsCount e) {
					e.printStackTrace();
					System.out.println("It is going to calculate considering the time from now!");
					ends.add(LocalDateTime.now());
					activity.sumUsefulTime(starts, ends);
					saveService(activity);
				}
			} else if (starts.size() == ends.size()) {
				activity.sumUsefulTime(starts, ends);
				saveService(activity);
			} else {
				throw new ActivityEndingTimeException(
						"Or the activity should have an ending time for each starting time or it should have subactivities. The activity Id is: "
								+ activity.getId() + ".");
			}
		} else {
			Duration tempUsefulTime = Duration.ZERO;
			for (Activity subActivity : subActivities.values()) {
				subActivity = calcUsefulTime(subActivity);
				tempUsefulTime = tempUsefulTime.plus(subActivity.getUsefulTime());
			}
			activity.setUsefulTime(tempUsefulTime);
			saveService(activity);
		}
		return activity;
	}
}
//
//} catch (ActivityEndingTimeException e) {
//	throw new ActivityEndingTimeException(e.getMessage());
