package allogica.trackingTimeDesktopApp.model.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;

@Repository
public interface ActivityEndRepository extends JpaRepository<ActivityEnd, Long> {
	List<ActivityEnd> findByTimeBetweenOrderByTimeAsc(LocalDateTime startDate, LocalDateTime endDate);
}
