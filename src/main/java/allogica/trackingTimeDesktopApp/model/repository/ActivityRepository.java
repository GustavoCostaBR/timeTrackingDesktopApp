package allogica.trackingTimeDesktopApp.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import allogica.trackingTimeDesktopApp.model.entity.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findByParentActivityId(Long parentId);
	
}
