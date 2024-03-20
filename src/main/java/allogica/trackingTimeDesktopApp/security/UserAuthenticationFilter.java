package allogica.trackingTimeDesktopApp.security;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import allogica.trackingTimeDesktopApp.model.Service.JwtTokenService;
import allogica.trackingTimeDesktopApp.model.entity.User;
import allogica.trackingTimeDesktopApp.model.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenService jwtTokenService; 

	@Autowired
	private UserRepository userRepository; 

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// It verifies if the endpoint needs authentication before processing the requisition
		if (checkIfEndpointIsNotPublic(request)) {
			String token = recoveryToken(request); // It recovers the token of the head "Authorization" of the requisition
			if (token != null) {
				String subject = jwtTokenService.getSubjectFromToken(token); // It gets the token subject (username in this case)
				User user = userRepository.findByUsername(subject).get(); // It gets the user by username
//                User userDetails = new User(user); // Only for code where the user itself does not implements the UserDetails interface

				// It creates a authentication object of Spring Security
				Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null,
						user.getAuthorities());

				// It defines the authentication object in the security context of Spring Security
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				throw new RuntimeException("O token está ausente.");
			}
		}
		filterChain.doFilter(request, response); // It continues the requisition processing 
	}

	 // It recovers the token of the head "Authorization" of the requisition
	private String recoveryToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null) {
			return authorizationHeader.replace("Bearer ", "");
		}
		return null;
	}

	// It verifies if the endpoint needs authentication before processing the requisition
	private boolean checkIfEndpointIsNotPublic(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		return !Arrays.asList(SecurityConfig.ENDPOINTS_WITH_AUTHENTICATION_NOT_REQUIRED).contains(requestURI);
	}

}
