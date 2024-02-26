package allogica.trackingTimeDesktopApp.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import allogica.trackingTimeDesktopApp.model.entity.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findByParentActivityId(Long parentId);
	
	@Query("SELECT a FROM Activity a WHERE a.current = true")
    Optional<Activity> findCurrentActivity();
	
}
