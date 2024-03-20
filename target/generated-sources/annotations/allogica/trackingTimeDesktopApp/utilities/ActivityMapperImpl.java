package allogica.trackingTimeDesktopApp.utilities;

import allogica.trackingTimeDesktopApp.DTOs.CreateActivityDto;
import allogica.trackingTimeDesktopApp.model.entity.Activity;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-20T17:02:35-0300",
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

        activity.setParentActivityId( dto.parentActivityId() );
        activity.setName( dto.name() );
        activity.setCurrent( dto.current() );
        activity.setDescription( dto.description() );
        activity.setTotalTime( dto.totalTime() );
        activity.setUsefulTime( dto.usefulTime() );
        if ( activity.getSubactivities() != null ) {
            List<Activity> list = dto.subactivities();
            if ( list != null ) {
                activity.getSubactivities().addAll( list );
            }
        }

        return activity;
    }
}
