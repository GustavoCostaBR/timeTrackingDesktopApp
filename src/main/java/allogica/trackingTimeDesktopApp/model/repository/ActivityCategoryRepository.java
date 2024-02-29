package allogica.trackingTimeDesktopApp.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import allogica.trackingTimeDesktopApp.model.entity.ActivityCategory;

@Repository
public interface ActivityCategoryRepository extends JpaRepository<ActivityCategory, Long> {
	@Query("SELECT c FROM ActivityCategory c WHERE c.name = :name")
    Optional<ActivityCategory> findByName(@Param("name") String name);
}
