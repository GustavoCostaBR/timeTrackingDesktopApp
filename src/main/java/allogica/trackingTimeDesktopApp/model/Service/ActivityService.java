package allogica.trackingTimeDesktopApp.model.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import allogica.trackingTimeDesktoppApp.exceptions.ActivityStartingTimeException;
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
				System.out.println(((Activity)(data1)).toString1());
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
		if (subactivities != null && !(subactivities.isEmpty())){
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
		Activity activityToDelete = entityManager.find(Activity.class, activityId);
		if (activityToDelete == null) {
			return; // Activity not found, nothing to delete
		}

		if (deleteSubActivities) {
			deleteActivityRecursively(activityToDelete);
		} else {
			handleSubActivitiesBeforeDeletion(activityToDelete);
			activityRepository.delete(activityToDelete); // Delete the activity itself
		}
	}

	private void deleteActivityRecursively(Activity activity) {
		TreeNode<Activity> activityTree = getAllSubactivitiesAsTree(activity.getId()); // Get full sub-tree

		// Traverse the tree in a post-order traversal (children first, then parent)
		for (TreeNode<Activity> child : activityTree.getChildren()) {
			deleteActivityRecursively(child.getData()); // Delete child nodes recursively
		}
		activityRepository.delete(activity); // Delete the activity itself
	}

	private void handleSubActivitiesBeforeDeletion(Activity activity) {
		List<Activity> firstLevelSubActivities = activityRepository.findByParentActivityId(activity.getId());

		// Check for nested sub-activities
		if (firstLevelSubActivities.stream().anyMatch(child -> !child.getSubactivities().isEmpty())) {
			throw new IllegalStateException("Cannot delete activity with nested sub-activities");
		}

		// Reassign first-level sub-activities to root
		for (Activity subactivity : firstLevelSubActivities) {
			subactivity.setParentActivityId(0L);
			saveActivity(subactivity); // Update in the database
		}
	}

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
	public void changeDescription(Long activityId, String description) throws ActivityNotFoundException {
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));
		activity.setDescription(description);
		activityRepository.save(activity);
	}

	@Transactional // Ensure transaction management
	public void serviceAddStart(Long activityId, LocalDateTime start) throws ActivityNotFoundException {
		// Retrieve the activity
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));

		// Create the ActivityStart entity
		ActivityStart subactivityStart = new ActivityStart(activity, start);

		// Save the ActivityStart using its repository
		activityStartRepository.save(subactivityStart);

		// Update the activity with the start time
		activity.addStart(start);

		// Save the updated activity
		activityRepository.save(activity);
	}

	@Transactional // Ensure transaction management
	public void serviceAddEnd(Long activityId, LocalDateTime end) throws ActivityNotFoundException {
		// Retrieve the activity
		Activity activity = activityRepository.findById(activityId)
				.orElseThrow(() -> new ActivityNotFoundException(activityId));

		// Create the ActivityStart entity
		ActivityEnd subactivityEnd = new ActivityEnd(activity, end);

		// Save the ActivityStart using its repository
		activityEndRepository.save(subactivityEnd);

		// Update the activity with the start time
		activity.addEnd(end);

		// Save the updated activity
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

	public void saveService(ActivityCategory activityCategory) {
		activityCategoryRepository.save(activityCategory);
	}

	public void saveService(Activity activity) {
		activityRepository.save(activity);
	}

	public void saveService(Activity activity, ActivityStart subactivityStart) {
		activityRepository.save(activity);
		activityStartRepository.save(subactivityStart);
	}

	public void saveService(Activity activity, ActivityEnd subactivityEnd) {
		activityRepository.save(activity);
		activityEndRepository.save(subactivityEnd);
	}

	public void saveService(Activity activity, ActivityStart subactivityStart, ActivityEnd subactivityEnd) {
		activityRepository.save(activity);
		activityStartRepository.save(subactivityStart);
		activityEndRepository.save(subactivityEnd);
	}

	public Activity stopsCurrentActivityService(Boolean state1) throws ActivityNotFoundException {
		Activity activity = stopCurrentActivity(state1);
		if (activity == null) {
			throw new ActivityNotFoundException();
		}
		return activity;
	}

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
	public List<TimeInterval> checkIntervalAvailability(LocalDate dayInput, Duration minInterval)
			throws CompromisedDataBaseException {
//		List <LocalDateTime> ends = findActivityEndTimesBetween(dayInput.atStartOfDay(), dayInput.plusDays(1).atStartOfDay());
//		List <LocalDateTime> starts = findActivityStartTimesBetween(dayInput.atStartOfDay(), dayInput.plusDays(1).atStartOfDay());

		List<ActivityEnd> ends = activityEndRepository.findByTimeBetweenOrderByTimeAsc(dayInput.atStartOfDay(),
				dayInput.plusDays(1).atStartOfDay());
		List<ActivityStart> starts = activityStartRepository.findByTimeBetweenOrderByTimeAsc(dayInput.atStartOfDay(),
				dayInput.plusDays(1).atStartOfDay());
		Boolean startOfDay = false;
		Boolean endOfDay = false;
		LocalDateTime temporaryStart = null;
		LocalDateTime temporaryEnd = null;
//		Checking if there wasn't a activity that started in one day and stopped in other
		if (ends.get(0).getActivity().getId() != starts.get(0).getActivity().getId()) {
			if (starts.get(0).getTime().isAfter(ends.get(0).getTime())) {
				temporaryEnd = ends.get(ends.size() - 1).getTime();
				ends.remove(0);
				startOfDay = true;
			}
		}
		if (starts.get(starts.size() - 1).getActivity().getId() != ends.get(ends.size() - 1).getActivity().getId()) {
			if (starts.get(starts.size() - 1).getTime().isAfter(ends.get(ends.size() - 1).getTime())) {
				temporaryStart = starts.get(starts.size() - 1).getTime();
				starts.remove(starts.size() - 1);
				endOfDay = true;
			}
		}
		if (starts.size() != ends.size()) {
			throw new CompromisedDataBaseException(
					"The number of Ends and Starts for a specific day should match after corrections, it is not the case. Verify the date "
							+ dayInput);
		}
		List<LocalDateTime> endsTime = new ArrayList<LocalDateTime>();
		List<LocalDateTime> startsTime = new ArrayList<LocalDateTime>();
		for (ActivityEnd end : ends) {
			endsTime.add(end.getTime());
		}
		for (ActivityStart start : starts) {
			startsTime.add(start.getTime());
		}
		List<TimeInterval> interval = new ArrayList<TimeInterval>();
		if (!(startOfDay)) {
			if (!(endOfDay)) {
//				Beginning of the day until the first start
				interval.add(new TimeInterval(startsTime.get(0).with(LocalTime.MIN), startsTime.get(0)));
				interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the ending of the day
				interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1), endsTime.get(0).with(LocalTime.MAX)));
			} else {
//				Beginning of the day until the first start
				interval.add(new TimeInterval(startsTime.get(0).with(LocalTime.MIN), startsTime.get(0)));
				interval = TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime);
//				Last end until the removed start
				interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1), temporaryStart));
			}
		} else {
			if (!(endOfDay)) {
//				From the removed end until the first start
				interval.add(new TimeInterval(temporaryEnd, startsTime.get(0)));
				interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the ending of the day
				interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1), endsTime.get(0).with(LocalTime.MAX)));
			} else {
//				From the removed end until the first start
				interval.add(new TimeInterval(temporaryEnd, startsTime.get(0)));
				interval.addAll(TimeInterval.addToInterval(startsTime.size(), endsTime, startsTime));
//				Last end until the removed start
				interval.add(new TimeInterval(endsTime.get(endsTime.size() - 1), temporaryStart));
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
		if (activity.getActivityStartCount() == 0) {
			throw new InvalidActivityInputException("Should have at least one start.");
		}
		activity.setCurrent(true);
		saveService(activity);

		return activity;
	}

	public void deleteActivityStartService(Activity activity, ActivityStart activityStart) {
		activity.deleteActivityStart(activityStart);
		activityStartRepository.delete(activityStart);
		saveService(activity);
	}

	public Activity getCurrentActvityService() throws ActivityNotFoundException {
		Optional<Activity> activity = activityRepository.findCurrentActivity();
		return activity.orElseThrow(() -> new ActivityNotFoundException());
	}

	public Activity addActivityStartService(Activity activity, ActivityStart activityStart, Duration minInterval,
			ActivityEnd activityEnd)
			throws ThereIsNoEndException, ThereIsNoStartException, CompromisedDataBaseException {
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
					stopsCurrentActivityService(false);
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
					return null;
				}
				activity.addStart(activityStart);
				activity.setCurrent(true);
				saveService(activity, activityStart);
				return activity;
			}
//		If the new activity being added manually starts after the last start of the currentActivity
			else if (activityStart.getTime().isAfter(currentActivity.getLastStart().getTime())) {
				deleteActivityEndService(currentActivity, currentActivity.getLastEnd());
				addActivityEndService(currentActivity, activityStart.getTime());
				try {
					stopsCurrentActivityService(false);
				} catch (ActivityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				activity.addStart(activityStart);
				activity.setCurrent(true);
				saveService(activity, (ActivityStart) activityStart);
				return activity;
			}
		}
//		If the currentActivity is not already done and has to be ended
		else if ((currentActivity.getActivityEndCount() + 1) == currentActivity.getActivityStartCount()) {
//		If the new activity being added manually starts after the last start of the currentActivity
			if (activityStart.getTime().isAfter(currentActivity.getLastStart().getTime())) {
				addActivityEndService(currentActivity, activityStart.getTime());
				try {
					stopsCurrentActivityService(false);
				} catch (ActivityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
				activity.addStart(activityStart);
				activity.setCurrent(true);
				saveService(activity, (ActivityStart) activityStart);
				return activity;
			}
		}
		List<TimeInterval> intervals = checkIntervalAvailability(activityStart.getTime().toLocalDate(), minInterval);
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
			saveService(activity, (ActivityStart) activityStart, activityEnd);
		}
		return activity;
	}

	public Activity addActivityStartService(Activity activity, ActivityStart activityTimeStart, Duration minInterval)
			throws ThereIsNoEndException, ThereIsNoStartException, CompromisedDataBaseException {
		activity = addActivityStartService(activity, activityTimeStart, minInterval, null);
		return activity;
	}

	@Transactional
	public void clearAllActivities() {
		List <Activity> allActivities = activityRepository.findAll();
		if (allActivities != null && !(allActivities.isEmpty())){
			for (Activity activity : allActivities) {
				delete(activity.getId(), true);
			}
		}
		
	}

//	To start an activity with time equals to now;
	@Transactional
	public Activity addActivityStartService(Activity activity) {
		ActivityStart newStart;
		try {
			Activity oldActivity = stopsCurrentActivityService(false);
			newStart = new ActivityStart(activity, oldActivity.getLastEnd().getTime());
			activity.addStart(newStart);
			activity.setCurrent(true);
//			Only saving activity by cascading it saves also the activity start
			saveService(activity);
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

	public void deleteActivityEndService(Activity activity, ActivityEnd activityEnd) {
		activity.deleteActivityEnd(activityEnd);
		activityEndRepository.delete(activityEnd);
		saveService(activity);
	}

	public Activity addActivityEndService(Activity activity, ActivityEnd activityEnd) {
		activity.addEnd(activityEnd);
		saveService(activity, activityEnd);
		return activity;
	}

	public Activity addActivityEndService(Activity activity, LocalDateTime endTime) {
		activity.addEnd(endTime);
		saveService(activity);
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
		List<Activity> subactivities = activity.getSubactivities();
		if (!(subactivities.isEmpty())) {
			end = LocalDateTime.of(1900, 1, 1, 0, 0);
			for (Activity subactivity : subactivities) {
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
		List<Activity> subActivities = activity.getSubactivities();
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
			for (Activity subActivity : subActivities) {
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
