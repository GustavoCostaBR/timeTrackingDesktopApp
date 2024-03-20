package allogica.trackingTimeDesktopApp.DTOs;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import allogica.trackingTimeDesktopApp.model.entity.Activity;
import allogica.trackingTimeDesktopApp.model.entity.ActivityCategory;
import allogica.trackingTimeDesktopApp.model.entity.ActivityEnd;
import allogica.trackingTimeDesktopApp.model.entity.ActivityStart;

public record CreateActivityDto(
		Long parentActivityId,
		String name,
		boolean current,
		Set<ActivityCategory> activityCategories,
		String description,
		List<ActivityStart> activityStarts,
		List<ActivityEnd> activityEnds,
		Duration totalTime,
		Duration usefulTime,
		List<Activity> subactivities,
		String action
		) {

}
