package campsite.reservations.core.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ScheduleDateAvailability {
    LocalDate date;
    boolean available;
}
