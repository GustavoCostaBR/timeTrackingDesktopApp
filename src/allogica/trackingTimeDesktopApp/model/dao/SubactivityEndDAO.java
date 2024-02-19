package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.entity.SubactivityEnd;

public class SubactivityEndDAO extends subactivityGenericDAO<SubactivityEnd> {
	
	public SubactivityEndDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
}
