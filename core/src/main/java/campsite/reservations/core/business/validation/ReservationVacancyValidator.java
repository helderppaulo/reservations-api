package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.repository.ScheduleRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
public class ReservationVacancyValidator implements ReservationValidator {

    private final ScheduleRepository scheduleRepository;

    @Override
    public boolean validate(final ReservationRequest request) {
        final LocalDate begin = request.getCheckInDate();
        final LocalDate end = request.getCheckOutDate();
        return scheduleRepository.retrieveReservedScheduleDates(begin, end, request.getReservationId()).isEmpty();
    }

    @Override
    public BusinessViolation violation() {
        return BusinessViolation.UNAVAILABLE_SELECTED_PERIOD;
    }
}
