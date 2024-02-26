package allogica.trackingTimeDesktopApp.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

public interface ActivityStartRepository extends JpaRepository<ActivityStart, Long> {
	
	
}
