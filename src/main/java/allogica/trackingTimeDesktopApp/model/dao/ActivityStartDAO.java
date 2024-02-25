package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

public class ActivityStartDAO extends ActivityGenericTimeDAO<ActivityStart> {
	
	public ActivityStartDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
}
