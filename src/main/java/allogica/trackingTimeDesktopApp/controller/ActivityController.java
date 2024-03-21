package allogica.trackingTimeDesktopApp.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import allogica.trackingTimeDesktopApp.DTOs.CreateActivityDto;
import allogica.trackingTimeDesktopApp.model.Service.ActivityService;
import allogica.trackingTimeDesktopApp.model.Service.CustomUserDetailsService;
import allogica.trackingTimeDesktopApp.model.Service.UserService;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.User;
import allogica.trackingTimeDesktopApp.utilities.ActivityMapper;
import allogica.trackingTimeDesktopApp.utilities.TimeParser;
import allogica.trackingTimeDesktoppApp.exceptions.CreateFirstActivityException;
import allogica.trackingTimeDesktoppApp.exceptions.InvalidActivityInputException;
import allogica.trackingTimeDesktoppApp.exceptions.ThereIsNoStartException;

@RestController
@RequestMapping("/activity")
public class ActivityController {
	@Autowired
	private UserService userService;

	@Autowired
	private CustomUserDetailsService userDetailsServiceImplementation;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ActivityMapper activityMapper;

	@PostMapping
	public ResponseEntity<?> createActivity(@RequestBody CreateActivityDto createActivityDto) {
//		It gets the User
		User currentUser = (User) userDetailsServiceImplementation
				.loadUserByUsername((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Activity activity = null;
//		System.out.println(createActivityDto.action());
		if (createActivityDto.action().equals("createFirstActivity")) {
//			It converts the data coming from a front end to a Activity class object
			activity = activityMapper.toEntity(createActivityDto);
			activity.setUser(currentUser);
//			If the CreateActivityDTO has any starting date  
			if (createActivityDto.activityStartsTime() == null || createActivityDto.activityStartsTime().size() == 0) {
				activity.addStart(LocalDateTime.now());
				try {
					activityService.createFirstActivity(activity, currentUser.getId());
				} catch (ThereIsNoStartException | InvalidActivityInputException | CreateFirstActivityException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					Map<String, String> responseBody = new HashMap<>();
					responseBody.put("error", e.getMessage());
					return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

				}

			} else {
				try {
					String dateTimeString = createActivityDto.activityStartsTime().get(0);
					
					LocalDateTime startingTime = TimeParser.parseStringToLocalDateTime(dateTimeString);
					activity.addStart(startingTime);
					activityService.createFirstActivity(activity, currentUser.getId());
				} catch (ThereIsNoStartException | InvalidActivityInputException | CreateFirstActivityException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					Map<String, String> responseBody = new HashMap<>();
					responseBody.put("error", e.getMessage());
					return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

				}
			}

		}

		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("Activity created:", activity.getName());
//		activityService.createFirstActivity(createActivityDto);
		return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
	}

//	@PostMapping("/login")
//	public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto) {
//		RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
//		return new ResponseEntity<>(token, HttpStatus.OK);
//	}

//	@GetMapping("/test")
//	public ResponseEntity<String> getAuthenticationTest() {
//		return new ResponseEntity<>("Succesfully anthenticated", HttpStatus.OK);
//	}
//
//	@GetMapping("/test/customer")
//	public ResponseEntity<String> getCustomerAuthenticationTest() {
//		return new ResponseEntity<>("Client succesfully authenticated", HttpStatus.OK);
//	}
//
//	@GetMapping("/test/administrator")
//	public ResponseEntity<String> getAdminAuthenticationTest() {
//		return new ResponseEntity<>("Admin succesfully authenticated", HttpStatus.OK);
//	}

}
