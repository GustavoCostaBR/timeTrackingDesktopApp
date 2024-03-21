package allogica.trackingTimeDesktopApp.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityCategory;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	List<Activity> findByParentActivityId(Long parentId);
	
//	List<Activity> findByParentActivityIdAndUser_Id(Long parentActivityId, Long userId);

	List<Activity> findByUser_Id(Long userId);
	
	@Query("SELECT a FROM Activity a WHERE a.current = true")
    Optional<Activity> findCurrentActivity();

//	Optional<Activity> findByIdAndUser_Id(Long id, Long userId);
	
//	@Transactional // Ensure transaction management
//    void deleteAll();
	
}
