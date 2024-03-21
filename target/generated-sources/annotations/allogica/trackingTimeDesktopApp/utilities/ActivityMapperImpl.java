package allogica.trackingTimeDesktopApp.utilities;

import allogica.trackingTimeDesktopApp.DTOs.CreateActivityDto;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-21T16:07:45-0300",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.36.0.v20231114-0937, environment: Java 17.0.10 (Eclipse Adoptium)"
)
@Component
public class ActivityMapperImpl implements ActivityMapper {

    @Override
    public Activity toEntity(CreateActivityDto dto) {
        if ( dto == null ) {
            return null;
        }

        Activity activity = new Activity();

        activity.setCurrent( dto.current() );
        activity.setDescription( dto.description() );
        activity.setName( dto.name() );
        activity.setParentActivityId( dto.parentActivityId() );
        activity.setTotalTime( dto.totalTime() );
        activity.setUsefulTime( dto.usefulTime() );

        return activity;
    }
}
