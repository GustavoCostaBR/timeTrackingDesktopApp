package allogica.trackingTimeDesktopApp.config;


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
		Activity activity2 = new Activity("Tema");
		activityService.addActivityStartService(activity2);
		Activity activity3 = new Activity("Tema da escola");
		ActivityCategory learning = activityService.createActivityCategoriesService("Learning");
		activity3.addCategories(learning);
		activityService.addActivityStartService(activity3);
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
		
//		subactivity1 = activityService.getActivityById(subactivity1.getId());
//		subactivity1.setParentActivityId(null);
//		activityService.saveActivity(subactivity1);
//		activityService.changeParentActivityId(activity.getId(), null);
		
		activityService.delete(activity.getId(), false);
		
		
//		System.out.println(activityService.getFirstLevelSubactivities(31L).getData());
//		System.out.println(activityService.getFirstLevelSubactivities(activity.getId()).getChildren());
		System.out.println("Tudo certo por aqui!");
		
		
//		activityService.saveActivity(activity);
//		System.out.println("Tudo rodando corretamente por aqui!");
	}
	
	
}
