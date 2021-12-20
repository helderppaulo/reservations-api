package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReservationPeriodValidator implements ReservationValidator {

    private static final long MINIMUM_TIME_AHEAD_DAYS = 1L;
    private static final long MAXIMUM_TIME_AHEAD_DAYS = 30L;

    @Override
    public boolean validate(final ReservationRequest request) {
        final LocalDate referenceDate = request.getReferenceDateTime().toLocalDate();
        long daysUntilCheckIn = ChronoUnit.DAYS.between(referenceDate, request.getCheckInDate());
        return daysUntilCheckIn >= MINIMUM_TIME_AHEAD_DAYS && daysUntilCheckIn <= MAXIMUM_TIME_AHEAD_DAYS;
    }

    @Override
    public BusinessViolation violation() {
        return BusinessViolation.INVALID_RESERVATION_PERIOD;
    }
}
