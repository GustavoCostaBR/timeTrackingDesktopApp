package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;

public class SubactivityEndDAO extends subactivityGenericDAO<ActivityEnd> {
	
	public SubactivityEndDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
