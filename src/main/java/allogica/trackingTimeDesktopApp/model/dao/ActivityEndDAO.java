package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;

public class ActivityEndDAO extends ActivityGenericTimeDAO<ActivityEnd> {
	
	public ActivityEndDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
}
