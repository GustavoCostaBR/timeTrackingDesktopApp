package allogica.trackingTimeDesktopApp.utilities;

import org.mapstruct.Mapper;

import allogica.trackingTimeDesktopApp.DTOs.CreateActivityDto;
import allogica.trackingTimeDesktopApp.model.entity.Activity;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

	Activity toEntity(CreateActivityDto dto);
	

//	Activity toEntity(CreateActivityDto dto, @Context CycleAvoidingMappingContext context);
}
