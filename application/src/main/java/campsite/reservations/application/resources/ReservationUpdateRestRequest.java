package campsite.reservations.application.resources;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationUpdateRestRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
