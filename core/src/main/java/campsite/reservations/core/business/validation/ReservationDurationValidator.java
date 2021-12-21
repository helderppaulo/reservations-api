package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;

import java.time.temporal.ChronoUnit;

public class ReservationDurationValidator implements ReservationValidator {

    private static final long MAXIMUM_RESERVATION_DURATION_DAYS = 3L;
    private static final long MINIMUM_RESERVATION_DURATION_DAYS = 1L;

    @Override
    public boolean validate(final ReservationRequest request) {
        final long days = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        return days <= MAXIMUM_RESERVATION_DURATION_DAYS && days >= MINIMUM_RESERVATION_DURATION_DAYS;
    }

    @Override
    public BusinessViolation violation() {
        return BusinessViolation.MAXIMUM_RESERVATION_DURATION_EXCEEDED;
    }
}
