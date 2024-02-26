package allogica.trackingTimeDesktopApp.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;

public interface ActivityEndRepository extends JpaRepository<ActivityEnd, Long> {
	
}
