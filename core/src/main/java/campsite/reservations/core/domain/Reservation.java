package campsite.reservations.core.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class Reservation {
    UUID id;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    ReservationStatus status;
    String customerName;
    String customerEmail;
}
