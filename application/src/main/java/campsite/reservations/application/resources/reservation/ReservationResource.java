package campsite.reservations.application.resources.reservation;

import campsite.reservations.application.resources.ResourceResult;
import campsite.reservations.application.resources.ResultResponseUnwrapper;
import campsite.reservations.core.ReservationScheduler;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.business.reservation.ReservationUpdateRequest;
import campsite.reservations.core.domain.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class ReservationResource {

    @Autowired
    private ReservationScheduler reservationScheduler;

    @PostMapping("/reservations")
    public ResponseEntity<ResourceResult<Reservation>> createReservation(
            @RequestBody final ReservationCreationRestRequest request
    ) {
        final Result<Reservation> result = Result.fromSupplier(() -> buildCreationRequest(request))
                .flatMap(reservationScheduler::createReservation);
        return ResultResponseUnwrapper.unwrap(result);
    }

    private ReservationCreationRequest buildCreationRequest(final ReservationCreationRestRequest requestBody) {
        return ReservationCreationRequest.builder()
                .checkInDate(requestBody.getCheckInDate())
                .checkOutDate(requestBody.getCheckOutDate())
                .customerEmail(requestBody.getCustomerEmail())
                .customerName(requestBody.getCustomerName())
                .referenceDateTime(LocalDateTime.now())
                .build();
    }

    @PatchMapping("/reservations/{id}")
    public ResponseEntity<ResourceResult<Reservation>> updateReservation(
            @PathVariable("id") final UUID reservationId,
            @RequestBody final ReservationUpdateRestRequest requestBody
    ) {
        final Result<Reservation> result = Result.fromSupplier(() -> buildUpdateRequest(reservationId, requestBody))
                .flatMap(reservationScheduler::updateReservation);
        return ResultResponseUnwrapper.unwrap(result);
    }

    private ReservationUpdateRequest buildUpdateRequest(
            final UUID reservationId,
            final ReservationUpdateRestRequest requestBody
    ) {
        return ReservationUpdateRequest.builder()
                .reservationId(reservationId)
                .checkInDate(requestBody.getCheckInDate())
                .checkOutDate(requestBody.getCheckOutDate())
                .referenceDateTime(LocalDateTime.now())
                .build();
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<ResourceResult<Reservation>> cancelReservation(
            @PathVariable("id") final UUID reservationId
    ) {
        final Result<Reservation> result = reservationScheduler.cancelReservation(reservationId);
        return ResultResponseUnwrapper.unwrap(result);
    }
}
