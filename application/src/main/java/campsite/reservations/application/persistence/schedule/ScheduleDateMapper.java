package campsite.reservations.application.persistence.schedule;

import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.application.persistence.reservation.ReservationMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ScheduleDateMapper {

    public static ScheduleDate fromEntity(final ScheduleDateEntity entity) {
        return ScheduleDate.builder()
                .date(entity.getDate())
                .reservation(ReservationMapper.fromEntity(entity.getReservation()))
                .build();
    }
}
