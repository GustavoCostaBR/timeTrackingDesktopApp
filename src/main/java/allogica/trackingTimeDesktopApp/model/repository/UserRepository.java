package allogica.trackingTimeDesktopApp.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import allogica.trackingTimeDesktopApp.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
	
//	@Transactional // Ensure transaction management
//    void deleteAll();
	
}
