package allogica.trackingTimeDesktopApp.model.Service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import allogica.trackingTimeDesktopApp.DTOs.CreateUserDto;
import allogica.trackingTimeDesktopApp.DTOs.LoginUserDto;
import allogica.trackingTimeDesktopApp.DTOs.RecoveryJwtTokenDto;
import allogica.trackingTimeDesktopApp.model.entity.Role;
import allogica.trackingTimeDesktopApp.model.entity.User;
import allogica.trackingTimeDesktopApp.model.repository.UserRepository;
import allogica.trackingTimeDesktopApp.security.SecurityConfig;

//import lombok.Builder;

@Service
public class UserService {

	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityConfig securityConfiguration;

    // Method responsible for user authentication and Token JWT return
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        // It creates a authentication object with email and password of the user
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.username(), loginUserDto.password());

        // It authenticates the user credentials
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // It gets the UserDetails object from the authenticated user
        User userDetails = (User) authentication.getPrincipal();

        // It generates a JWT token for the authenticated user
        return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
    }

    // Method responsible for user creation/registration
    public void createUser(CreateUserDto createUserDto) {

        // It creates a new user with the data input
        User newUser = User.builder()
                .username(createUserDto.username())
                // It encrypts the user password with the bcrypt algorithm
                .password(securityConfiguration.passwordEncoder().encode(createUserDto.password()))
                // It assigns a specific permission to the user
                .roles(Set.of(Role.builder().name(createUserDto.role()).build()))
                .build();

        // It saves the new user in the database
        userRepository.save(newUser);
    }
}
