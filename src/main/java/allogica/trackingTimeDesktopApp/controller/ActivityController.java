package allogica.trackingTimeDesktopApp.controller;

import java.time.LocalDateTime;
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

import allogica.trackingTimeDesktopApp.DTOs.CreateActivityDto;
import allogica.trackingTimeDesktopApp.model.Service.ActivityService;
import allogica.trackingTimeDesktopApp.model.Service.CustomUserDetailsService;
import allogica.trackingTimeDesktopApp.model.Service.UserService;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;
import allogica.trackingTimeDesktopApp.model.entity.User;
import allogica.trackingTimeDesktopApp.utilities.ActivityMapper;
import allogica.trackingTimeDesktoppApp.exceptions.CompromisedDataBaseException;
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
//		System.out.println(SecurityContextHolder.getContext().getAuthentication());
		User currentUser = (User) userDetailsServiceImplementation
				.loadUserByUsername((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		Activity activity = null;
		System.out.println(createActivityDto.action());
		if (createActivityDto.action().equals("createFirstActivity")) {
			System.out.println("ABAcate florido");
			activity = activityMapper.toEntity(createActivityDto);
			activity.setUser(currentUser);
			System.out.println(activity);
			if (activity.getActivityStartCount() == 0) {

				activity.addStart(LocalDateTime.now());

				try {
					activityService.createFirstActivity(activity);
				} catch (ThereIsNoStartException | InvalidActivityInputException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					Map<String, String> responseBody = new HashMap<>();
					responseBody.put("error", e.getMessage());

					return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

				}

			} else {
				try {
					activityService.createFirstActivity(activity);
				} catch (ThereIsNoStartException | InvalidActivityInputException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
					Map<String, String> responseBody = new HashMap<>();
					responseBody.put("error", e.getMessage());
					return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

				}
			}

		}

		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("error", activity.getName());
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
