package campsite.reservations.core.business.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface ReservationRequest {

    UUID getReservationId();

    LocalDateTime getReferenceDateTime();

    LocalDate getCheckInDate();

    LocalDate getCheckOutDate();
}
