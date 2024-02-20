package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

public abstract class ActivityGenericTimeDAO<T> extends GenericDAO<T> {

	public ActivityGenericTimeDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void saveGenericActivityTime(T subactivity) {
		super.saveOrUpdate(subactivity);
	}

}