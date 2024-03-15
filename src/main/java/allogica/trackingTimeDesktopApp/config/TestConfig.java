package allogica.trackingTimeDesktopApp.config;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import allogica.trackingTimeDesktopApp.model.Service.ActivityService;
import allogica.trackingTimeDesktopApp.model.Service.ActivityService.TreeNode;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityCategory;
import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {
	
//	@Autowired
//	private ActivityRepository activityRepository;
//	
//	@Autowired
//	private ActivityStartRepository activityStartRepository;
//	
//	@Autowired
//	private ActivityEndRepository activityEndRepository;
	
	@Autowired
	private ActivityService activityService;
	

	@Override
	public void run(String... args) throws Exception {
		
		activityService.clearAllActivities();
		Activity activity = new Activity("Trabalho de Casa");
		activity.addStart(LocalDateTime.now());
		activityService.createFirstActivity(activity);
//		ActivityEnd activityEnd = new ActivityEnd(activity, LocalDateTime.now().plusHours(7));
//		activityService.addActivityEndService(activity, activityEnd);
		
		
		
		
		Activity activityTest1 = new Activity("Tarefa do dia anterior");
		ActivityStart activityStartTest1 = new ActivityStart(activityTest1, LocalDateTime.now().minusDays(1));
		ActivityEnd activityEndTest1 = new ActivityEnd(activityTest1, LocalDateTime.now().minusHours(7));
		activityService.addActivityStartService(activityTest1, activityStartTest1, Duration.ofMinutes(3), activityEndTest1);
//		activityService.addActivityStartService(activityTest1, activityStartTest1, Duration.ofMinutes(3), null);
		
		Activity activity2 = new Activity("Tema");
		activityService.addActivityStartService(activity2);
		Activity activity3 = new Activity("Tema da escola");
		ActivityCategory learning = activityService.createActivityCategoriesService("Learning");
		ActivityCategory playing = activityService.createActivityCategoriesService("Playing");
		activity3.addCategories(learning);
		activityService.addActivityStartService(activity3);
		activityService.addActivityEndService(activity3);
		
		
		
		activity3.addCategories(learning);
//		activityService.addActivityStartService(activity3);
		activity = activityService.getActivityById(activity.getId());
		activity.addCategories(learning);
		activityService.saveService(activity);
		Activity subactivity1 = new Activity("Tema de matemática");
		subactivity1.setParentActivityId(activity.getId());
		subactivity1.addCategories(learning);
		activityService.addActivityStartService(subactivity1);
		
		
		Activity subactivity2 = new Activity("Questão 1 do Tema de matemática");
		subactivity2.setParentActivityId(subactivity1.getId());
		subactivity2.addCategories(learning);
		activityService.addActivityStartService(subactivity2);
		
		Activity subactivity2a = new Activity("Subquestão A da questão 1 do Tema de matemática");
		subactivity2a.setParentActivityId(subactivity2.getId());
		subactivity2a.addCategories(learning);
		activityService.addActivityStartService(subactivity2a);
//		Hibernate.initialize(activity.getCategories());
//		
//		for (Activity subactivity : activity.getSubactivities()) {
//			Hibernate.initialize(subactivity.getCategories());
//			Hibernate.initialize(subactivity.getStart());
//			Hibernate.initialize(subactivity.getEnd());
//			Hibernate.initialize(subactivity.getSubactivities());
//			System.out.println(subactivity);
//		}
		
//		
//		
//		System.out.println(activity.getId());
//		System.out.println(activityService.getFirstLevelSubactivities(activity.getId()).getData());
		
//		for (TreeNode<Activity> subActivity : activityService.getFirstLevelSubactivities(1L).getChildren()) {
//			System.out.println(subActivity.getData());
//		}
		
		TreeNode<Activity> activityNode = activityService.getAllSubactivitiesAsTree(activity.getId());
		activity = activityService.getActivityById(activity.getId());
		activityNode.setData(activity);
		
		activityNode.printTreeNode();
//		
//		subactivity1 = activityService.getActivityById(subactivity1.getId());
//		subactivity1.setParentActivityId(null);
//		activityService.saveActivity(subactivity1);
//		activityService.changeParentActivityId(activity.getId(), null);
		
//		activityService.delete(activity.getId(), false);
		activityService.delete(subactivity1.getId(), false);
		activity = activityService.getActivityById(activity.getId());
		activityNode = activityService.getAllSubactivitiesAsTree(activity.getId());
		activityNode.setData(activity);
		activityNode.printTreeNode();
		
		activityService.stopsCurrentActivityService(true);
		
		activityService.changeDescription(activity.getId(), "All working fine here");
		activityService.changeDescription(subactivity2.getId(), "Subactivity working as well");
		activityService.changeName(subactivity2.getId(), "Change name function is working");
		
		
		
		Activity subactivity2b = new Activity("Subquestão b da questão 1 do Tema de matemática");
		subactivity2b.setParentActivityId(subactivity2.getId());
		subactivity2b.addCategories(learning);
		ActivityStart activityStartSubactivity2b = new ActivityStart(subactivity2b, subactivity2a.getLastStart().getTime().plusNanos(10000));
		activityService.addActivityStartService(subactivity2b, activityStartSubactivity2b, null);
		
		subactivity2b.addCategories(playing);
		activityService.saveActivity(subactivity2b);
//		activityService.delete(subactivity2.getId(), false);
		
		
		
		Activity subactivity2c = new Activity("Subquestão c da questão 1 do Tema de matemática");
		subactivity2c.setParentActivityId(subactivity2.getId());
		subactivity2c.addCategories(learning);
		ActivityStart activityStartSubactivity2c = new ActivityStart(subactivity2c, subactivity2b.getLastStart().getTime().plusNanos(11000));
		activityService.addActivityStartService(subactivity2c, activityStartSubactivity2c, null);
		
		
		Activity subactivity2d = new Activity("Subquestão d da questão 1 do Tema de matemática");
		subactivity2d.setParentActivityId(subactivity2.getId());
		subactivity2d.addCategories(learning);
		ActivityStart activityStartSubactivity2d = new ActivityStart(subactivity2d, subactivity2c.getLastStart().getTime().minusHours(3));
		activityService.addActivityStartService(subactivity2d, activityStartSubactivity2d, Duration.ofMinutes(3));
		
		
		Activity subactivity2e = new Activity("Subquestão e da questão 1 do Tema de matemática");
		subactivity2e.setParentActivityId(subactivity2.getId());
		subactivity2e.addCategories(learning);
		ActivityStart activityStartSubactivity2e = new ActivityStart(subactivity2e, subactivity2c.getLastStart().getTime().plusHours(3));
		ActivityEnd activityEndSubactivity2e = new ActivityEnd(subactivity2e, subactivity2c.getLastStart().getTime().plusDays(1));
		activityService.addActivityStartService(subactivity2e, activityStartSubactivity2e, Duration.ofMinutes(3), activityEndSubactivity2e);
//		
		
		
		subactivity2a = activityService.getActivityById(subactivity2a.getId());
//		subactivity2a = activityService.deleteActivityStartService(subactivity2a, subactivity2a.getLastStart());
		
		ActivityStart activityStartSubactivity2a = new ActivityStart(subactivity2a, subactivity2c.getLastStart().getTime().plusHours(4));
		ActivityEnd activityEndSubactivity2a = new ActivityEnd(subactivity2a, subactivity2c.getLastStart().getTime().plusHours(5));
		
		
		subactivity2a = activityService.addActivityStartService(subactivity2a, activityStartSubactivity2a, Duration.ofMinutes(5), activityEndSubactivity2a);
		
		
		subactivity2c = activityService.getActivityById(subactivity2c.getId());
		ActivityStart activityStartSubactivity2cc = new ActivityStart(subactivity2c, subactivity2c.getLastStart().getTime().minusDays(5));
		activityService.addActivityStartService(subactivity2c, activityStartSubactivity2cc, Duration.ofMinutes(3));
		
//		System.out.println(activityService.getFirstLevelSubactivities(31L).getData());
//		System.out.println(activityService.getFirstLevelSubactivities(activity.getId()).getChildren());
		System.out.println("Tudo certo por aqui!");
		
		Activity activityTestLongDuration = new Activity("Tarefa longaTeste");
		ActivityStart activityStartTestLongDuration = new ActivityStart(activityTestLongDuration, LocalDateTime.now().minusYears(10));
		ActivityEnd activityEndTestLongDuration = new ActivityEnd(activityTestLongDuration, LocalDateTime.now().minusDays(7));
		activityService.addActivityStartService(activityTestLongDuration, activityStartTestLongDuration, Duration.ofMinutes(3), activityEndTestLongDuration);
		
		
//		System.out.println(activityService.calcEnd(activity));
//		System.out.println(LocalDateTime.MIN);
		System.out.println(activityService.calcTotalTime(activity));
		System.out.println(activityService.calcTotalTime(activityTestLongDuration));
		
		System.out.println(activityService.calcUsefulTime(activity.getId()));
		
//		activityService.saveActivity(activity);
//		System.out.println("Tudo rodando corretamente por aqui!");
	}
	
	
}
