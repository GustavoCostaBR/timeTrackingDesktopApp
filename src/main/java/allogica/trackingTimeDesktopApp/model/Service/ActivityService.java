package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import allogica.trackingTimeDesktopApp.DTOs.CreateActivityDto;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityCategory;
import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;
import allogica.trackingTimeDesktopApp.model.entity.TimeInterval;
import allogica.trackingTimeDesktopApp.model.repository.ActivityCategoryRepository;
import allogica.trackingTimeDesktopApp.model.repository.ActivityEndRepository;
import allogica.trackingTimeDesktopApp.model.repository.ActivityRepository;
import allogica.trackingTimeDesktopApp.model.repository.ActivityStartRepository;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityEndingTimeException;
import allogica.trackingTimeDesktoppApp.exceptions.ActivityNotFoundException;
import allogica.trackingTimeDesktoppApp.exceptions.CompromisedDataBaseException;
import allogica.trackingTimeDesktoppApp.exceptions.IncompatibleStartsEndsCount;
import allogica.trackingTimeDesktoppApp.exceptions.InvalidActivityInputException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoEndException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoStartException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class ActivityService {

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private ActivityCategoryRepository activityCategoryRepository;

	@Autowired
	private ActivityStartRepository activityStartRepository;

	@Autowired
	private ActivityEndRepository activityEndRepository;

	@Autowired
	private EntityManager entityManager;

	@Transactional
	public Activity getActivityById(Long id) {
		Activity activity = activityRepository.findById(id).orElse(null);
		if (activity != null) {
			Hibernate.initialize(activity.getCategories());
			Hibernate.initialize(activity.getStart());
			Hibernate.initialize(activity.getEnd());
			Hibernate.initialize(activity.getSubactivities());
		}
		return activity;

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

		public void printTreeNode() {
			T data1 = this.getData();
			if (data1 != null) {
				System.out.println(((Activity) (data1)).toString1());
			}
			List<TreeNode<T>> childreen = this.getChildren();
			if (childreen != null && !(childreen.isEmpty())) {
//				System.out.println(childreen.size());
//				int contador = 0;
				for (TreeNode<T> subactivity : childreen) {
//					contador++;
//					System.out.println(contador);
					subactivity.printTreeNode();

				}
			}

		}

		public String toStringChildren() {
			return "TreeNode [children=" + children + "]";
		}

		@Override
		public String toString() {
			return "TreeNode [data=" + data + ", children=" + children + ", parent=" + parent + "]";
		}

		public TreeNode(T data, List<TreeNode<T>> children, TreeNode<T> parent) {
			this.data = data;
			this.children = children != null ? children : new ArrayList<>();
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

	public ActivityCategory createActivityCategories(String name) {
		ActivityCategory category1 = new ActivityCategory();
		category1.setName(name);
		return category1;
	}

	public ActivityCategory createActivityCategoriesService(String name) {
		ActivityCategory activityCategory = createActivityCategories(name);
		Optional<ActivityCategory> oldActivityCategory;
		oldActivityCategory = activityCategoryRepository.findByName(activityCategory.getName());
		if (oldActivityCategory.isPresent()) {
			activityCategory = oldActivityCategory.get();
			return activityCategory;
		}
		saveService(activityCategory);
		return activityCategory;
	}

	@Transactional
	public TreeNode<Activity> getFirstLevelSubactivities(Long activityId) {
		TreeNode<Activity> rootNode = null;
		rootNode = getFirstLevelSubactivities(activityId, rootNode);
		return rootNode;
	}

	@Transactional
	public TreeNode<Activity> getFirstLevelSubactivities(Long activityId, TreeNode<Activity> rootNode) {
		List<Activity> subactivities = activityRepository.findByParentActivityId(activityId);
		if (rootNode == null) {
			rootNode = new TreeNode<>();
		}
		if (subactivities != null && !(subactivities.isEmpty())) {
			for (Activity subactivity : subactivities) {
				Hibernate.initialize(subactivity.getCategories());
				Hibernate.initialize(subactivity.getStart());
				Hibernate.initialize(subactivity.getEnd());
//				There is no sense in initializing the subactivities here, this method is not for it.
				Hibernate.initialize(subactivity.getSubactivities());
			}

		}
		for (Activity subactivity : subactivities) {
			TreeNode<Activity> childNode = new TreeNode<>(subactivity, new ArrayList<>(), rootNode);
			rootNode.addChildren(childNode);
		}
		return rootNode;
	}

	@Transactional
	public TreeNode<Activity> getAllSubactivitiesAsTree(Long activityId) {
		TreeNode<Activity> rootNode = null;
		rootNode = getAllSubactivitiesAsTree(activityId, rootNode);
		return rootNode;
	}

	@Transactional
	public TreeNode<Activity> getAllSubactivitiesAsTree(Long activityId, TreeNode<Activity> rootNode) {
		if (rootNode == null) {
			rootNode = getFirstLevelSubactivities(activityId);
		} else {
			rootNode = getFirstLevelSubactivities(activityId, rootNode);
		}
		// Recursively process each child node separately
		for (TreeNode<Activity> child : rootNode.getChildren()) {
			child = (this.getAllSubactivitiesAsTree(child.getData().getId(), child));
//			child.addChildren(getAllSubactivitiesAsTree(child.getData().getId(), child));	
		}
		return rootNode;
	}

	@Transactional // Ensure JPA transaction management
	public void delete(Long activityId, Boolean deleteSubActivities) {
//		Activity activityToDelete = entityManager.find(Activity.class, activityId);
		Activity activityToDelete = getActivityById(activityId);
		if (activityToDelete == null) {
			return; // Activity not found, nothing to delete
		}

		if (deleteSubActivities) {
			deleteActivityRecursively(activityToDelete);
		} else {
			activityToDelete = handleSubActivitiesBeforeDeletion(activityToDelete);

			activityRepository.delete(activityToDelete); // Delete the activity itself
		}
	}

	@Transactional
	private void deleteActivityRecursively(Activity activity) {
		TreeNode<Activity> activityTree = getAllSubactivitiesAsTree(activity.getId()); // Get full sub-tree

		// Traverse the tree in a post-order traversal (children first, then parent)
		for (TreeNode<Activity> child : activityTree.getChildren()) {
			deleteActivityRecursively(child.getData()); // Delete child nodes recursively
		}
		activityRepository.delete(activity); // Delete the activity itself
	}

	@Transactional
	private Activity handleSubActivitiesBeforeDeletion(Activity activity) {
		TreeNode<Activity> activityTreeNode = getFirstLevelSubactivities(activity.getId());
		List<TreeNode<Activity>> firstLevelSubActivities = activityTreeNode.getChildren();

//		List<Activity> firstLevelSubActivities = activityRepository.findByParentActivityId(activity.getId());

		// Check for nested sub-activities
//		if (!(firstLevelSubActivities.isEmpty())) {
//			throw new IllegalStateException("Cannot delete activity with nested sub-activities");
//		}

		// Reassign first-level sub-activities to root
		for (TreeNode<Activity> childTreeNode : firstLevelSubActivities) {
//			Ensuring it is an updated item and managed in this session
			Activity tempSubActivity = getActivityById(childTreeNode.getData().getId());
			tempSubActivity.setParentActivityId(activity.getParentActivityId());
//			changeParentActivityId(tempSubActivity.getParentActivityId(), null);
			saveActivity(tempSubActivity); // Update in the database
		}
//		Deleting subactivities from the entity
		activity.deleteAllSubactitivies();
		return activity;

	}

	@Transactional
	public void saveActivity(Activity activity) {
		activityRepository.save(activity); // Saves or updates based on ID
	}

	@Transactional // Ensure transaction management
	public void changeParentActivityId(Long parentActivityId, Long newParentActivityId) {
		Query query = entityManager.createQuery(
				"UPDATE Activity a SET a.parentActivityId = :newParentId WHERE a.parentActivityId = :oldParentId");
		query.setParameter("newParentId", newParentActivityId);
		query.setParameter("oldParentId", parentActivityId);
		query.executeUpdate(); // Perform the update
	}

	@Transactional // Ensure transaction management
	public Activity stopCurrentActivity(Boolean state1) {
		// Find the current activity
		Query query = entityManager.createQuery("SELECT a FROM Activity a WHERE a.current = :state");
		query.setParameter("state", true); // Assuming "current" is a boolean flag set to true
		Activity activity = (Activity) query.getSingleResult();

		if ((activity.getActivityEndCount() + 1) == activity.getActivityStartCount()) {
			activity.addEnd(LocalDateTime.now());
		}

		if (activity != null) {
			// Update activity data
			activity.setCurrent(state1);
//	        entityManager.merge(activity); // Persist changes
			saveActivity(activity);
		}
		return activity;
	}
	
	@Transactional // Ensure transaction management
	public Activity stopCurrentActivity(Boolean state1, Activity currentActivity) {
		if ((currentActivity.getActivityEndCount() + 1) == currentActivity.getActivityStartCount()) {
			currentActivity.addEnd(LocalDateTime.now());
		}
		if (currentActivity != null) {
			// Update activity data
			currentActivity.setCurrent(state1);
//	        entityManager.merge(activity); // Persist changes
			saveActivity(currentActivity);
		}
		return currentActivity;
	}

	@Transactional
	public Activity stopsCurrentActivityService(Boolean state1) throws ActivityNotFoundException {
		Activity activity = stopCurrentActivity(state1);
		if (activity == null) {
			throw new ActivityNotFoundException();
		}
		return activity;
	}
	
	@Transactional
	public Activity stopsCurrentActivityService(Boolean state1, Activity currentActivity) throws ActivityNotFoundException {
		Activity activity = stopCurrentActivity(state1, currentActivity);
		if (activity == null) {
			throw new ActivityNotFoundException();
		}
		return activity;
	}

	@Transactional // Ensure transaction management
	public void changeDescription(Long activityId, String description) throws ActivityNotFoundException {
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));
		activity.setDescription(description);
		activityRepository.save(activity);
	}

	@Transactional // Ensure transaction management
	public void changeName(Long activityId, String name) throws ActivityNotFoundException {
		// Retrieve the activity
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));

		// Update the name
		activity.setName(name);

		// Save the updated activity
		activityRepository.save(activity);
	}

	@Transactional // Ensure transaction management
	public void changeTotalTime(Long activityId, Duration tempo) throws ActivityNotFoundException {
		// Retrieve the activity
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));

		// Update the total time
		activity.setTotalTime(tempo);

		// Save the updated activity
		activityRepository.save(activity);
	}

	@Transactional // Ensure transaction management
	public Activity addStart(Long activityId, LocalDateTime start) throws ActivityNotFoundException {
		// Retrieve the activity
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));
		
		// Update the activity with the start time
		activity.addStart(start);

		activity.setCurrent(true);
		
		// Save the updated activity
		activityRepository.save(activity);
		return activity;
	}

	@Transactional // Ensure transaction management
	public Activity addStart(Activity activity, ActivityStart start) throws ActivityNotFoundException {
		// Save the ActivityStart using its repository
//		activityStartRepository.save(start);

		// Update the activity with the start time
		activity.addStart(start);

		activity.setCurrent(true);

		// Save the updated activity
		saveService(activity);
		return activity;
	}

	public void saveService(ActivityCategory activityCategory) {
		activityCategoryRepository.save(activityCategory);
	}

	public void saveService(Activity activity) {
		activityRepository.save(activity);
	}

	public void saveService(ActivityStart subactivityStart) {
//		activityRepository.save(activity);
		activityStartRepository.save(subactivityStart);
	}

	public void saveService(ActivityEnd subactivityEnd) {
//		activityRepository.save(activity);
		activityEndRepository.save(subactivityEnd);
	}

//	public void saveService(Activity activity, ActivityStart subactivityStart, ActivityEnd subactivityEnd) {
//		activityRepository.save(activity);
//		activityStartRepository.save(subactivityStart);
//		activityEndRepository.save(subactivityEnd);
//	}

	public List<LocalDateTime> findActivityStartTimesBetween(LocalDateTime dayInput, LocalDateTime dayInput2) {
		LocalDateTime startDate = dayInput;
		LocalDateTime endDate = dayInput2;
		return entityManager.createQuery(
				"SELECT as.time FROM ActivityStart as WHERE as.time BETWEEN :startDate AND :endDate ORDER BY as.time ASC",
				LocalDateTime.class).setParameter("startDate", startDate).setParameter("endDate", endDate)
				.getResultList();
	}

	public List<LocalDateTime> findActivityEndTimesBetween(LocalDateTime dayInput, LocalDateTime dayInput2) {
		LocalDateTime startDate = dayInput;
		LocalDateTime endDate = dayInput2;
		return entityManager.createQuery(
				"SELECT as.time FROM ActivityEnd as WHERE as.time BETWEEN :startDate AND :endDate ORDER BY as.time ASC",
				LocalDateTime.class).setParameter("startDate", startDate).setParameter("endDate", endDate)
				.getResultList();
	}

	@Transactional
	public List<TimeInterval> checkIntervalAvailability(LocalDate dayInputStart, LocalDate dayInputEnd,
			Duration minInterval)
			throws CompromisedDataBaseException, ThereIsNoStartException, ActivityNotFoundException {
//		List <LocalDateTime> ends = findActivityEndTimesBetween(dayInput.atStartOfDay(), dayInput.plusDays(1).atStartOfDay());
//		List <LocalDateTime> starts = findActivityStartTimesBetween(dayInput.atStartOfDay(), dayInput.plusDays(1).atStartOfDay());
		List<ActivityEnd> ends;
		List<ActivityStart> starts;
		Boolean endDayIsTheSameOfStartDayMarker = false;

		if (dayInputEnd.isEqual(dayInputStart)) {
			endDayIsTheSameOfStartDayMarker = true;
			ends = activityEndRepository.findByTimeBetweenOrderByTimeAsc(dayInputStart.atStartOfDay(),
					dayInputEnd.plusDays(2).atStartOfDay());
			starts = activityStartRepository.findByTimeBetweenOrderByTimeAsc(dayInputStart.atStartOfDay(),
					dayInputEnd.plusDays(2).atStartOfDay());
		} else {
			ends = activityEndRepository.findByTimeBetweenOrderByTimeAsc(dayInputStart.atStartOfDay(),
					dayInputEnd.plusDays(1).atStartOfDay());
			starts = activityStartRepository.findByTimeBetweenOrderByTimeAsc(dayInputStart.atStartOfDay(),
					dayInputEnd.plusDays(1).atStartOfDay());
		}

		Boolean startOfDay = false;
		Boolean endOfDay = false;
		LocalDateTime temporaryStart = null;
		LocalDateTime temporaryEnd = null;
//		Boolean markerIfInputDayIsToday = false;
//		Checking if the current activity is finished before checking:
		Activity currentActivity = getCurrentActvityService();
//		Boolean thereIsNoActivityMarker = false;

		List<TimeInterval> interval = new ArrayList<TimeInterval>();

//		If there is no activity for the selected days
		if (starts.isEmpty() && ends.isEmpty()) {
			interval.add(new TimeInterval(dayInputStart.atStartOfDay(), dayInputEnd.atTime(LocalTime.MAX)));
		}
//		If there are activities for the selected days
		else {
//			Testing to see if the current not finished activity occurs in the same day of the start or end of the new activity
			if (endDayIsTheSameOfStartDayMarker) {
				if (currentActivity.getLastStart().getTime().toLocalDate().isAfter(dayInputStart.minusDays(1))
						&& currentActivity.getLastStart().getTime().toLocalDate().isBefore(dayInputEnd.plusDays(3))) {
					if (currentActivity.getActivityStartCount() == currentActivity.getActivityEndCount() + 1) {
						ends.add(new ActivityEnd(currentActivity, LocalDateTime.now()));
//					markerIfInputDayIsToday = true;
					}
				}
			} else {
				if (currentActivity.getLastStart().getTime().toLocalDate().isAfter(dayInputStart.minusDays(1))
						&& currentActivity.getLastStart().getTime().toLocalDate().isBefore(dayInputEnd.plusDays(2))) {

					if (currentActivity.getActivityStartCount() == currentActivity.getActivityEndCount() + 1) {
						ends.add(new ActivityEnd(currentActivity, LocalDateTime.now()));
//						markerIfInputDayIsToday = true;
					}

				}

			}

//		Checking if there wasn't a activity that started in one day and stopped in other outside of the interval range
			if (ends.get(0).getActivity().getId() != starts.get(0).getActivity().getId()) {
				if (starts.get(0).getTime().isAfter(ends.get(0).getTime())) {
//					temporaryEnd = ends.get(ends.size() - 1).getTime();
					temporaryEnd = ends.get(0).getTime();
					ends.remove(0);
					startOfDay = true;
				}
			}
			
			if (starts.get(starts.size() - 1).getActivity().getId() != ends.get(ends.size() - 1).getActivity()
					.getId()) {
				if (starts.get(starts.size() - 1).getTime().isAfter(ends.get(ends.size() - 1).getTime())) {
					temporaryStart = starts.get(starts.size() - 1).getTime();
					starts.remove(starts.size() - 1);
					endOfDay = true;
				}
			}
			int counter1 = 0;
			int counter2 = 0;
			for (ActivityStart start : starts) {
				System.out.println("The start number" + counter1 + " starts at: " + start.getTime());
				counter1++;
			}

			for (ActivityEnd end : ends) {
				System.out.println("The end number" + counter2 + " ends at: " + end.getTime());
				counter2++;
			}

			if (starts.size() != ends.size()) {
				throw new CompromisedDataBaseException(
						"The number of Ends and Starts for a specific day should match after corrections, it is not the case. Verify the date "
								+ dayInputStart);
			}
			List<LocalDateTime> endsTime = new ArrayList<LocalDateTime>();
			List<LocalDateTime> startsTime = new ArrayList<LocalDateTime>();
			for (ActivityEnd end : ends) {
				endsTime.add(end.getTime());
			}
			for (ActivityStart start : starts) {
				startsTime.add(start.getTime());
			}
//			There is no specific correction for the start of the first day
			if (!(startOfDay)) {
//				There is no specific correction for the end of the last day
				if (!(endOfDay)) {
//				Beginning of the "starting" day until the first start
					interval.add(new TimeInterval(dayInputStart.atStartOfDay(), startsTime.get(0)));
					interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
					if (endDayIsTheSameOfStartDayMarker) {
//						Last end until the end of the ending day
						interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1),
								dayInputEnd.plusDays(1).atTime(LocalTime.MAX)));
					} else {
//						Last end until the end of the ending day
						interval.add(
								new TimeInterval(endsTime.get(endsTime.size() - 1), dayInputEnd.atTime(LocalTime.MAX)));
					}
//				There are corrections for the end of the last day
				} else {
//				Beginning of the day until the first start
					interval.add(new TimeInterval(dayInputStart.atStartOfDay(), startsTime.get(0)));
					interval = TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime);
//				Last end until the removed start
					interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1), temporaryStart));
				}
//			There are corrections for the start of the first day
			} else {
//				There is no specific correction for the end of the last day
				if (!(endOfDay)) {
//				From the removed end until the first start
					interval.add(new TimeInterval(temporaryEnd, startsTime.get(0)));
					interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the ending of the day
					
					if (endDayIsTheSameOfStartDayMarker) {
//						Last end until the end of the ending day
						interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1),
								dayInputEnd.plusDays(1).atTime(LocalTime.MAX)));
					} else {
//						Last end until the end of the ending day
						interval.add(
								new TimeInterval(endsTime.get(endsTime.size() - 1), dayInputEnd.atTime(LocalTime.MAX)));
					}
				}
//				There are corrections for the end of the last day
				else {
//				From the removed end until the first start
					interval.add(new TimeInterval(temporaryEnd, startsTime.get(0)));
					interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the removed start
					interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1), temporaryStart));
				}
			}
		}
		TimeInterval.removeIntervalLessThan(interval, minInterval);
		return interval;
	}

	public Activity createFirstActivity(Activity activity)
			throws ThereIsNoStartException, InvalidActivityInputException {
		if (activity.getName() == null) {
			throw new InvalidActivityInputException("Invalid activity name");
		}
//		Getting the number of activity starts from the DTO
		if (activity.getActivityStartCount() == 0) {
			throw new InvalidActivityInputException("Should have at least one start.");
		}
		activity.setCurrent(true);
		
		saveService(activity);

		return activity;
	}

	@Transactional
	public Activity deleteActivityStartService(Activity activity, ActivityStart activityStart) {
		activity.deleteActivityStart(activityStart);
		activityStartRepository.delete(activityStart);
		saveService(activity);
		return activity;
	}

	public Activity getCurrentActvityService() throws ActivityNotFoundException {
		Optional<Activity> activity = activityRepository.findCurrentActivity();
		return activity.orElseThrow(() -> new ActivityNotFoundException());
	}

	@Transactional
	public Activity addActivityStartService(Activity activity, ActivityStart activityStart, Duration minInterval,
			ActivityEnd activityEnd) throws ThereIsNoEndException, ThereIsNoStartException,
			CompromisedDataBaseException, ActivityNotFoundException {
		Activity currentActivity;
		try {
			currentActivity = getCurrentActvityService();
		} catch (ActivityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
//		If the currentActivity is already finished
		if (currentActivity.getActivityEndCount() == currentActivity.getActivityStartCount()) {
//		If the new activity being added manually starts after the end of the currentActivity
			if (activityStart.getTime().isAfter(currentActivity.getLastEnd().getTime())) {
				try {
					stopsCurrentActivityService(false, currentActivity);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return null;
				}

				activity = addStart(activity, activityStart);
				if (activityEnd != null) {
					activity = addActivityEndService(activity, activityEnd);
					}
//				activity.addStart(activityStart);
//				activity.setCurrent(true);
//				saveService(activity, activityStart);
				return activity;
			}
//		If the new activity being added manually starts after the last start of the currentActivity
			else if (activityStart.getTime().isAfter(currentActivity.getLastStart().getTime())) {
				currentActivity = deleteActivityEndService(currentActivity, currentActivity.getLastEnd());
				currentActivity = addActivityEndService(currentActivity, activityStart.getTime());
				
				
				try {
					currentActivity = stopsCurrentActivityService(false, currentActivity);
				} catch (ActivityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				activity = addStart(activity, activityStart);
				
				if (activityEnd != null) {
					activity = addActivityEndService(activity, activityEnd);
					}

//				activity.addStart(activityStart);
//				activity.setCurrent(true);
//				saveService(activity, (ActivityStart) activityStart);
				return activity;
			}
		}
//		If the currentActivity is not already done and has to be ended
		else if ((currentActivity.getActivityEndCount() + 1) == currentActivity.getActivityStartCount()) {
//		If the new activity being added manually starts after the last start of the currentActivity
			if (activityStart.getTime().isAfter(currentActivity.getLastStart().getTime())) {
				currentActivity = addActivityEndService(currentActivity, activityStart.getTime());
				try {
					currentActivity = stopsCurrentActivityService(false, currentActivity);
				} catch (ActivityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}

				activity = addStart(activity, activityStart);
				
				if (activityEnd != null) {
					activity = addActivityEndService(activity, activityEnd);
				}
//				activity.addStart(activityStart);
//				activity.setCurrent(true);
//				saveService(activity, (ActivityStart) activityStart);
				return activity;
			}
		}
//		Any other situation is leading the program to check interval availability;
		List<TimeInterval> intervals;
		if (activityEnd != null) {
			intervals = checkIntervalAvailability(activityStart.getTime().toLocalDate(),
					activityEnd.getTime().toLocalDate(), minInterval);
		} else {
			intervals = checkIntervalAvailability(activityStart.getTime().toLocalDate(),
					activityStart.getTime().toLocalDate(), minInterval);
		}
		TimeInterval.printTimeInterval(intervals);
		TimeInterval answer = null;
		Boolean checker = false;
		if (activityEnd == null) {
			answer = TimeInterval.checksIfListContainsThatStart(intervals, activityStart.getTime());
		} else {
			answer = TimeInterval.checksIfListContainsThatInterval(intervals, activityStart.getTime(),
					activityEnd.getTime());
			checker = true;
		}
		if (answer != null) {
			activity.addStart(activityStart);
			if (checker == false) {
				activityEnd = new ActivityEnd(activity, answer.getEnd());
			}
			activity.addEnd(activityEnd);
			saveService(activity);

//			saveService(activity, (ActivityStart) activityStart, activityEnd);
		}
		return activity;
	}

	@Transactional
	public Activity addActivityStartService(Activity activity, ActivityStart activityTimeStart, Duration minInterval)
			throws ThereIsNoEndException, ThereIsNoStartException, CompromisedDataBaseException,
			ActivityNotFoundException {
		activity = addActivityStartService(activity, activityTimeStart, minInterval, null);
		return activity;
	}

	@Transactional
	public void clearAllActivities() {
		List<Activity> allActivities = activityRepository.findAll();
		if (allActivities != null && !(allActivities.isEmpty())) {
			for (Activity activity : allActivities) {
				delete(activity.getId(), true);
			}
		}

	}

//	To start an activity with time equals to now;
	@Transactional
	public Activity addActivityStartService(Activity activity)
			throws ThereIsNoStartException, CompromisedDataBaseException {
		ActivityStart newStart;
		try {
			Activity oldActivity = stopsCurrentActivityService(true);
			newStart = new ActivityStart(activity, oldActivity.getLastEnd().getTime());
			addActivityStartService(activity, newStart, null);
//			activity.addStart(newStart);
//			activity.setCurrent(true);
//			Only saving activity by cascading it saves also the activity start
//			saveService(activity);
		} catch (ThereIsNoEndException e) {
			System.out.println(
					"Serious. It's not supposed to happen! The problem happend in the addActivityStartService");
			e.printStackTrace();
			return null;
		} catch (ActivityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return activity;
	}

	public Activity deleteActivityEndService(Activity activity, ActivityEnd activityEnd) {
		activity.deleteActivityEnd(activityEnd);
		activityEndRepository.delete(activityEnd);
		saveService(activity);
		return activity;
	}

	@Transactional
	public Activity addActivityEndService(Activity activity, ActivityEnd activityEnd) {
		activity.addEnd(activityEnd);
//		saveService(activity);
		saveService(activityEnd);
		return activity;
	}

	@Transactional
	public Activity addActivityEndService(Activity activity, LocalDateTime endTime) {
		ActivityEnd newEnd = new ActivityEnd(activity, endTime);
		activity.addEnd(newEnd);
//		saveService(activity);
		saveService(newEnd);
		return activity;
	}

	@Transactional
	public Activity addActivityEndService(Activity activity) {
		ActivityEnd newEnd = new ActivityEnd(activity, LocalDateTime.now());
		activity.addEnd(newEnd);
		saveService(newEnd);
		return activity;
	}

	
	
//	I changed the approach to don't really set the end in database but to dinamically calculate it. If I set it in the database I will broke the checkIntervalsAvailabilityFunction
	@Transactional
	public Map<Long, LocalDateTime> calcStart(Activity activity) throws ThereIsNoStartException {
		Map<Long, LocalDateTime> activitiesStartingTime = new HashMap<>();
//		I do not know why I have used the following if statement
//		if (end != null && (activity.getActivityStartCount() == activity.getActivityEndCount())) {
//			return activity;
//		}
//		Starting verification of subactivities ends if it is not complete in the activity itself
		activity = getActivityById(activity.getId());
		List<Activity> subactivities = activity.getSubactivities();
		for (Activity subActivity : subactivities) {
			if (subActivity.getSubactivities().isEmpty()) {
				LocalDateTime subActivityGetFirstStartGetTime;
				subActivityGetFirstStartGetTime = subActivity.getLFirstTemporalStart();
				activitiesStartingTime.put(subActivity.getId(), subActivityGetFirstStartGetTime);
				continue;

			}
			else {
				activitiesStartingTime.putAll(calcStart(subActivity));
			}
		}
		
		LocalDateTime temp;
		try {
			temp = activity.getLFirstTemporalStart();
		} catch (ThereIsNoStartException e) {
			e.printStackTrace();
			temp = LocalDateTime.MAX;
		}
		
		for (Map.Entry<Long, LocalDateTime> entry : activitiesStartingTime.entrySet()) {
			if (entry.getValue().isBefore(temp)) {
				temp = entry.getValue();
			}
		}
		
		activitiesStartingTime.put(activity.getId(), temp);
		
		return activitiesStartingTime;

	}
	
	
//	I changed the approach to don't really set the end in database but to dinamically calculate it. If I set it in the database I will broke the checkIntervalsAvailabilityFunction
	@Transactional
	public Map<Long, LocalDateTime> calcEnd(Activity activity) {
		Map<Long, LocalDateTime> activitiesEndingTime = new HashMap<>();
//		I do not know why I have used the following if statement
//		if (end != null && (activity.getActivityStartCount() == activity.getActivityEndCount())) {
//			return activity;
//		}
//		Starting verification of subactivities ends if it is not complete in the activity itself
		activity = getActivityById(activity.getId());
		List<Activity> subactivities = activity.getSubactivities();
		for (Activity subActivity : subactivities) {
			if (subActivity.getSubactivities().isEmpty()) {
				LocalDateTime subActivityGetLastEndGetTime;
				try {
					subActivityGetLastEndGetTime = subActivity.getLastTemporalEnd();
				} catch (ThereIsNoEndException e) {
					e.printStackTrace();
					subActivityGetLastEndGetTime = LocalDateTime.now();
				}
				activitiesEndingTime.put(subActivity.getId(), subActivityGetLastEndGetTime);
				continue;
			}
			else {
				activitiesEndingTime.putAll(calcEnd(subActivity));
			}
		}
		
		LocalDateTime temp;
		try {
			temp = activity.getLastTemporalEnd();
		} catch (ThereIsNoEndException e) {
			e.printStackTrace();
			temp = LocalDateTime.MIN;
		}
		
		for (Map.Entry<Long, LocalDateTime> entry : activitiesEndingTime.entrySet()) {
			if (entry.getValue().isAfter(temp)) {
				temp = entry.getValue();
			}
		}
		
		activitiesEndingTime.put(activity.getId(), temp);
		
		return activitiesEndingTime;

	}

	
	@Transactional
	public Activity setActivityAndSubactivitiesTotalTime(Activity activity, Map<Long, LocalDateTime> temporaryEndList,  Map<Long, LocalDateTime> temporaryStartList) throws ThereIsNoStartException {
//		activity = getActivityById(activity.getId());
		List<Activity> subactivities = activity.getSubactivities();
		
		if (!(subactivities.isEmpty())) {
			for (Activity subactivity : subactivities) {
				subactivity = setActivityAndSubactivitiesTotalTime(subactivity, temporaryEndList, temporaryStartList);
			}
		}
			activity.deleteAllSubactitivies();
			activity.setSubActivities(subactivities);
			LocalDateTime start = temporaryStartList.get(activity.getId());
			LocalDateTime end = temporaryEndList.get(activity.getId());
			Duration activityTotalTime = Duration.between(start, end);
			if (activity.getTotalTime() != activityTotalTime) {
				activity.setTotalTime(activityTotalTime);
			}
			
		return activity;
	}
	
	
	@Transactional
	public Activity calcTotalTime(Activity activity) throws ThereIsNoStartException, ThereIsNoEndException {
		activity = getActivityById(activity.getId());
		if (activity.getActivityStartCount() != 0) {
			if ((activity.getActivityStartCount() == (activity.getActivityEndCount() + 1)) || (activity.getActivityStartCount() == (activity.getActivityEndCount()))) {
				Map<Long, LocalDateTime> temporaryEndList;
				Map<Long, LocalDateTime> temporaryStartList;
					temporaryEndList = calcEnd(activity);
					temporaryStartList = calcStart(activity);
//					System.out.println(temporary);
					activity = setActivityAndSubactivitiesTotalTime(activity, temporaryEndList, temporaryStartList);
					System.out.println(temporaryEndList);
					System.out.println(temporaryStartList);
//					saveService(activity);
					return activity;

				}
				}
		return activity;
		}
//		It will never get to this point, but for the eclipse IDE to be happy...
		
	@Transactional
	public Activity calcUsefulTime(Long activityId) throws ActivityEndingTimeException, IncompatibleStartsEndsCount {
		Activity activity = getActivityById(activityId);
		List<Activity> subActivities = activity.getSubactivities();
		List<LocalDateTime> ends = activity.getEndTime();
		List<LocalDateTime> starts = activity.getStartTime();
		if (subActivities.isEmpty()) {
			if (starts.size() == ends.size() + 1) {
					ends.add(LocalDateTime.now());
					if (starts.size() == ends.size()) {
						activity.sumUsefulTime(starts, ends);
//						saveService(activity);
					}
			} else if (starts.size() == ends.size()) {
				activity.sumUsefulTime(starts, ends);
//				saveService(activity);
			} else {
				throw new ActivityEndingTimeException(
						"Or the activity should have an ending time for each starting time or it should have subactivities. The activity Id is: "
								+ activity.getId() + ".");
			}
		} else {
			Duration tempUsefulTime = Duration.ZERO;
			for (Activity subActivity : subActivities) {
				subActivity = calcUsefulTime(subActivity.getId());
				tempUsefulTime = tempUsefulTime.plus(subActivity.getUsefulTime());
			}
			activity.setUsefulTime(tempUsefulTime);
//			saveService(activity);
		}
		return activity;
	}
}
