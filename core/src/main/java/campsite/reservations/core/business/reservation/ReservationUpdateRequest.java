package campsite.reservations.core.business.reservation;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class ReservationUpdateRequest implements ReservationRequest {
    UUID reservationId;
    LocalDateTime referenceDateTime;
    LocalDate checkInDate;
    LocalDate checkOutDate;
}
