package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;

public class ReservationConsistencyValidator implements ReservationValidator {

    @Override
    public boolean validate(final ReservationRequest request) {
        return request.getCheckInDate().isBefore(request.getCheckOutDate());
    }

    @Override
    public BusinessViolation violation() {
        return BusinessViolation.INCONSISTENT_RESERVATION_DATES;
    }
}
