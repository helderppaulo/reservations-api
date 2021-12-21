package campsite.reservations.application.persistence.schedule;

import campsite.reservations.core.domain.ScheduleControl;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScheduleControlMapper {

    public static ScheduleControlEntity toEntity(final ScheduleControl scheduleControl) {
        return ScheduleControlEntity.builder()
                .id(scheduleControl.getId())
                .lastExecution(scheduleControl.getLastExecution())
                .lastScheduleDate(scheduleControl.getLastScheduleDate())
                .build();
    }

    public static ScheduleControl fromEntity(final ScheduleControlEntity entity) {
        return ScheduleControl.builder()
                .id(entity.getId())
                .lastExecution(entity.getLastExecution())
                .lastScheduleDate(entity.getLastScheduleDate()).build();
    }

}
