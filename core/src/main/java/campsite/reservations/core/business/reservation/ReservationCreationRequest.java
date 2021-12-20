package campsite.reservations.core.business.reservation;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public class ReservationCreationRequest implements ReservationRequest {

    LocalDateTime referenceDateTime;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String customerName;
    String customerEmail;

    @Override
    public UUID getReservationId() {
        return null;
    }
}
