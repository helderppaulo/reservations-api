package campsite.reservations.core.business.schedule;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class ScheduleInquiryRequest {
    LocalDateTime referenceDateTime;
    LocalDate begin;
    LocalDate end;
}
