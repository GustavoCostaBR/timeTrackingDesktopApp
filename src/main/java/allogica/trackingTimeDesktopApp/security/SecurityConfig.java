package allogica.trackingTimeDesktopApp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private UserAuthenticationFilter userAuthenticationFilter;

//	Endpoints with not required autenthication
	public static final String[] ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED = { "/users/login", // url to login
			"/users" // url to create a new user
	};

//	Endpoints with required autenthication
	public static final String [] ENDPOINTS_WITH_AUTHENTICATION_REQUIRED = {
            "/users/test",
            "/activity"
    };
	
	// Endpoints that can only be accessed by users with client permission
	public static final String[] ENDPOINTS_CUSTOMER = { "/users/test/customer", "/activity" };

	// Endpoints that can only be accessed by users with admin permission
	public static final String[] ENDPOINTS_ADMIN = { "/users/test/administrator" };

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
        		.sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configure stateless session
        		.authorizeHttpRequests(authorize -> authorize // Configure authorization for http requisition
                .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).permitAll()
                .requestMatchers(ENDPOINTS_WITH_AUTHENTICATION_REQUIRED).authenticated()
                .requestMatchers(ENDPOINTS_ADMIN).hasRole("ADMINISTRATOR") // It is not necessary to put "ROLE" before the role
                .requestMatchers(ENDPOINTS_CUSTOMER).hasRole("CUSTOMER")
                .anyRequest().denyAll()) 
                .addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // Add the users autenthication filter we've made before the standard filter of spring security
                .build();
    }

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
//	private final CustomUserDetailsService userDetailsService;
//	private final BCryptPasswordEncoder passwordEncoder;
//
//	public SecurityConfig(CustomUserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
//		this.userDetailsService = userDetailsService;
//		this.passwordEncoder = passwordEncoder;
//		
//	}
//
//
//	
//	@Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().authenticated())
//                .userDetailsService(userDetailsService) // Directly set userDetailsService
//                .passwordEncoder(passwordEncoder).and()  // Directly set passwordEncoder
//                .build();
//    }
//
//	@Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}