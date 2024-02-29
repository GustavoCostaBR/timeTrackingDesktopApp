package allogica.trackingTimeDesktopApp.config;


import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import allogica.trackingTimeDesktopApp.model.Service.ActivityService;
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
		
//		activityService.saveActivity(activity);
//		System.out.println("Tudo rodando corretamente por aqui!");
	}
	
	
}
