package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;

import java.time.Period;
import java.time.temporal.ChronoUnit;

public class ReservationDurationValidator implements ReservationValidator {

    private static final long MAXIMUM_RESERVATION_DURATION_DAYS = 3L;

    @Override
    public boolean validate(final ReservationRequest request) {
        final Period period = Period.between(request.getCheckInDate(), request.getCheckOutDate());
        return period.get(ChronoUnit.DAYS) <= MAXIMUM_RESERVATION_DURATION_DAYS;
    }

    @Override
    public BusinessViolation violation() {
        return BusinessViolation.MAXIMUM_RESERVATION_DURATION_EXCEEDED;
    }
}
