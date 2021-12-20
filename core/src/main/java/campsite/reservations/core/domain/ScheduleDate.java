package campsite.reservations.core.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ScheduleDate {
    LocalDate date;
    Reservation reservation;
}
