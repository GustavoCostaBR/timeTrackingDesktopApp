package allogica.trackingTimeDesktopApp.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

@Repository
public interface ActivityStartRepository extends JpaRepository<ActivityStart, Long> {
	List<ActivityStart> findByTimeBetweenOrderByTimeAsc(LocalDateTime startDate, LocalDateTime endDate);
}
