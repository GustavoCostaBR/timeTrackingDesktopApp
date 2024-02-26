package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;
import allogica.trackingTimeDesktopApp.model.entity.ActivityTime;
import allogica.trackingTimeDesktopApp.model.entity.TimeInterval;
import allogica.trackingTimeDesktopApp.model.repository.ActivityEndDAO;
import allogica.trackingTimeDesktopApp.model.repository.ActivityRepository;
import allogica.trackingTimeDesktopApp.model.repository.ActivityStartDAO;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityEndingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityStartingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.CompromisedDataBaseException;
import allogica.trackingTimeDesktoppApp.exceptions.IncompatibleStartsEndsCount;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoEndException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoStartException;

@Service
public class ActivityService {
	private ActivityRepository dao;
	private ActivityEndDAO daoEnd;
	private ActivityStartDAO daoStart;

	@Autowired
	private ActivityRepository activityRepository;
	
	public Activity getActivityById(Long id) {
		return activityRepository.findById(id).orElse(null);
	}
	
	
	public class TreeNode<T> {
	    private T data;   
		private List<TreeNode<T>> children;
	    private TreeNode<T> parent;
	    
	    public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public List<TreeNode<T>> getChildren() {
			return children;
		}

		public void setChildren(List<TreeNode<T>> children) {
			this.children = children;
		}
		
		public void addChildren(TreeNode<T> child) {
			this.children.add(child);
		}

		public TreeNode<T> getParent() {
			return parent;
		}

		public void setParent(TreeNode<T> parent) {
			this.parent = parent;
		}

		public TreeNode(T data, List<TreeNode<T>> children, TreeNode<T> parent) {
			this.data = data;
			this.children = children;
			this.parent = parent;
		}

		public TreeNode(List<TreeNode<T>> children, TreeNode<T> parent) {
			this(null, children, parent);
		}
		
		public TreeNode(List<TreeNode<T>> children) {
			this(null, children, null);
		}
		
		public TreeNode() {
			this(null, new ArrayList<>(), null);
		}
	    
	}
	
	public TreeNode<Activity> getFirstLevelSubactivities(Long activityId) {
        TreeNode<Activity> rootNode = null;
        rootNode = getFirstLevelSubactivities(activityId, rootNode);
        return rootNode;
    }
	
	public TreeNode<Activity> getFirstLevelSubactivities(Long activityId, TreeNode<Activity> rootNode) {
        List<Activity> subactivities = activityRepository.findByParentActivityId(activityId);
        if (rootNode == null) {
        	rootNode = new TreeNode<>();	
        }
        for (Activity subactivity : subactivities) {
        	TreeNode<Activity> childNode = new TreeNode<>(subactivity, new ArrayList<>(), rootNode);
            rootNode.addChildren(childNode);
        }
        return rootNode;
    }
	
	public TreeNode<Activity> getAllSubactivitiesAsTree(Long activityId) {
	    TreeNode<Activity> rootNode = getFirstLevelSubactivities(activityId);
	    // Recursively process each child node separately
	    for (TreeNode<Activity> child : rootNode.getChildren()) {
	        child.addChildren(getAllSubactivitiesAsTree(child.getData().getId()));
	    }
	    return rootNode;
	}
		
	public ActivityService(SessionFactory sessionFactory) {
		dao = new ActivityRepository(sessionFactory);
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
	
	public List<TimeInterval> checkIntervalAvailability(LocalDate dayInput, Duration minInterval) throws CompromisedDataBaseException {
		List <ActivityEnd> ends = daoEnd.findByDateRange(ActivityEnd.class, dayInput, dayInput.plusDays(1));
		List <ActivityStart> starts = daoStart.findByDateRange(ActivityStart.class, dayInput, dayInput.plusDays(1));
		Boolean startOfDay = false;
		Boolean endOfDay = false;
		LocalDateTime temporaryStart = null;
		LocalDateTime temporaryEnd = null;
//		Checking if there wasn't a activity that started in one day and stopped in other
		if (ends.get(0).getActivity().getId() != starts.get(0).getActivity().getId()) {
			if (starts.get(0).getTime().isAfter(ends.get(0).getTime())) {
				temporaryEnd = ends.get(ends.size()-1).getTime();
				ends.remove(0);
				startOfDay = true;
			}
		}
		if (starts.get(starts.size()-1).getActivity().getId() != ends.get(ends.size()-1).getActivity().getId()) {
			if (starts.get(starts.size()-1).getTime().isAfter(ends.get(ends.size()-1).getTime())) {
				temporaryStart = starts.get(starts.size()-1).getTime();
				starts.remove(starts.size()-1);
				endOfDay = true;
		}}
		if (starts.size() != ends.size()) {
			throw new CompromisedDataBaseException("The number of Ends and Starts for a specific day should match after corrections, it is not the case. Verify the date " + dayInput);
		}
		List <LocalDateTime> endsTime = new ArrayList<LocalDateTime>();
		List <LocalDateTime> startsTime = new ArrayList<LocalDateTime>();
		for (ActivityEnd end : ends) {
			endsTime.add(end.getTime());
		}
		for (ActivityStart start : starts) {
			startsTime.add(start.getTime());
		}
		List <TimeInterval> interval = new ArrayList<TimeInterval>();
		if (!(startOfDay)) {
			if (!(endOfDay)) {
//				Beginning of the day until the first start
				interval.add(new TimeInterval(startsTime.get(0).with(LocalTime.MIN), startsTime.get(0)));
				interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the ending of the day
				interval.add(new TimeInterval(endsTime.get(endsTime.size()-1), endsTime.get(0).with(LocalTime.MAX)));
				}
			else {
//				Beginning of the day until the first start
				interval.add(new TimeInterval(startsTime.get(0).with(LocalTime.MIN), startsTime.get(0)));
				interval = TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime);
//				Last end until the removed start
				interval.add(new TimeInterval(endsTime.get(endsTime.size()-1), temporaryStart));	
		}
	}
		else {
			if (!(endOfDay)) {
//				From the removed end until the first start
				interval.add(new TimeInterval(temporaryEnd, startsTime.get(0)));
				interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the ending of the day
				interval.add(new TimeInterval(endsTime.get(endsTime.size()-1), endsTime.get(0).with(LocalTime.MAX)));
}
			else {
//				From the removed end until the first start
				interval.add(new TimeInterval(temporaryEnd, startsTime.get(0)));
				interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the removed start
				interval.add(new TimeInterval(endsTime.get(endsTime.size()-1), temporaryStart));
			}
		}
		TimeInterval.removeIntervalLessThan(interval, minInterval);
		return interval;
		}
			
	
	
	public void deleteActivityStartService(Activity activity, ActivityStart activityStart) {
		activity.deleteActivityStart(activityStart);
		daoStart.delete(activityStart);
		saveService(activity);
	}
	
	public Activity addActivityStartService(Activity activity, ActivityTime activityTimeStart, Duration minInterval, ActivityEnd activityEnd) throws ThereIsNoEndException, ThereIsNoStartException, CompromisedDataBaseException {
		Activity currentActivity = dao.findByProperty(Activity.class, "current", true).get(0);
//		If the currentActivity is already finished
		if (currentActivity.getActivityEndCount() == currentActivity.getActivityStartCount()) {
//		If the new activity being added manually starts after the end of the currentActivity
			if (activityTimeStart.getTime().isAfter(currentActivity.getLastEnd().getTime())) {
				stopsCurrentActivityService(false);
				activity.addStart(activityTimeStart);
				activity.setCurrent(true);
				saveService(activity, (ActivityStart)activityTimeStart);
				return activity;
			}
//		If the new activity being added manually starts after the last start of the currentActivity
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
//		If the currentActivity is not already done and has to be ended
		else if ((currentActivity.getActivityEndCount() + 1) == currentActivity.getActivityStartCount()) {
//		If the new activity being added manually starts after the last start of the currentActivity
			if (activityTimeStart.getTime().isAfter(currentActivity.getLastStart().getTime()))  {
				addActivityEndService(currentActivity, (ActivityEnd)activityTimeStart);
				stopsCurrentActivityService(false);
				activity.addStart(activityTimeStart);
				activity.setCurrent(true);
				saveService(activity, (ActivityStart)activityTimeStart);
				return activity;
		}	
		}
		List<TimeInterval> intervals =  checkIntervalAvailability(activityTimeStart.getTime().toLocalDate(), minInterval);
		TimeInterval answer = null;
		Boolean checker = false;
		if (activityEnd == null) {
		answer = TimeInterval.checksIfListContainsThatStart(intervals, activityTimeStart.getTime());
		}
		else {
			answer = TimeInterval.checksIfListContainsThatInterval(intervals, activityTimeStart.getTime(), activityEnd.getTime());
			checker = true;
		}
		if (answer != null){
			activity.addStart(activityTimeStart);
			if (checker == false) {
				activityEnd = new ActivityEnd(activity, answer.getEnd());
			}
			activity.addEnd(activityEnd);
			saveService(activity, (ActivityStart)activityTimeStart, activityEnd);
		}
		return activity;
	}	
	
	public Activity addActivityStartService(Activity activity, ActivityTime activityTimeStart, Duration minInterval) throws ThereIsNoEndException, ThereIsNoStartException, CompromisedDataBaseException {
		activity = addActivityStartService(activity, activityTimeStart, minInterval, null);
		return activity;
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
