package campsite.reservations.core.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class ScheduleControl {
    UUID id;
    LocalDate lastScheduleDate;
    LocalDate lastExecution;
}
