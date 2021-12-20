package campsite.reservations.application.resources;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationCreationRestRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String customerName;
    private String customerEmail;
}