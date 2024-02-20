package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

public class SubactivityStartDAO extends subactivityGenericDAO<ActivityStart> {
	
	public SubactivityStartDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
