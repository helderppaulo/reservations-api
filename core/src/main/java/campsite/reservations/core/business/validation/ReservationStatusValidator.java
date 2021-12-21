package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.repository.ReservationRepository;
import lombok.AllArgsConstructor;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
public class ReservationStatusValidator implements ReservationValidator {

    private final ReservationRepository repository;

    @Override
    public boolean validate(final ReservationRequest request) {
        return ofNullable(request.getReservationId())
                .flatMap(repository::fetchReservation)
                .filter((r) -> r.getStatus() == ReservationStatus.CANCELLED)
                .isEmpty();
    }

    @Override
    public BusinessViolation violation() {
        return BusinessViolation.CANCELLED_RESERVATION;
    }
}
