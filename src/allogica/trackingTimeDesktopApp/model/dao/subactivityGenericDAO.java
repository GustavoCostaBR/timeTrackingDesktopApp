package allogica.trackingTimeDesktopApp.model.dao;

import org.hibernate.SessionFactory;

public abstract class subactivityGenericDAO<T> extends GenericDAO<T> {

	public subactivityGenericDAO(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void saveGenericSubactivityTime(T subactivity) {
		this.saveOrUpdate(subactivity);
	}

}