package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.SubactivityStart;

public class SubactivityStartDAO extends subactivityGenericDAO<SubactivityStart> {
	
	public SubactivityStartDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
