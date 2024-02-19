package allogica.trackingTimeDesktopApp.model.Service;

import org.hibernate.SessionFactory;

import allogica.trackingTimeDesktopApp.model.dao.ActivityDAO;

public class ActivityService{
	private ActivityDAO dao;
	
	public ActivityService(SessionFactory sessionFactory) {
		this.dao = new ActivityDAO(sessionFactory);
	}
}
