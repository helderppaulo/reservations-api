package campsite.reservations.core.business.reservation;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.validation.ReservationValidationExecutor;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ReservationCreationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final TransactionProvider transactionProvider;
    private final ReservationValidationExecutor validator;

    public Result<Reservation> createReservation(final ReservationCreationRequest request) {
        return Result.successful(request)
                .flatMap(this::validate)
                .map(this::saveReservationAtomically);
    }

    private Result<ReservationCreationRequest> validate(final ReservationCreationRequest request) {
        final List<BusinessViolation> violations = this.validator.validate(request);
        if (violations.isEmpty())
            return Result.successful(request);
        else
            return Result.businessError(violations);
    }

    private Reservation saveReservationAtomically(final ReservationCreationRequest request) {
        return transactionProvider.executeAtomically(() -> {
            final Reservation reservation = reservationRepository.saveReservation(buildReservation(request));
            updateSchedule(reservation.getId(), request.getCheckInDate(), request.getCheckOutDate());
            return reservation;
        });
    }

    private void checkScheduleVacancy(final List<ScheduleDate> dates, final LocalDate begin, final LocalDate end) {
        final long scheduleDays = ChronoUnit.DAYS.between(begin, end);
        if (scheduleDays != dates.size()) throw new RuntimeException("inconsistent schedule");
    }

    private void updateSchedule(final UUID reservationId, final LocalDate begin, final LocalDate end) {
        final List<ScheduleDate> dates = scheduleRepository.retrieveUnreservedScheduleDatesLocking(begin, end, null);
        checkScheduleVacancy(dates, begin, end);
        dates.forEach(date -> scheduleRepository.updateScheduleDateReservation(date.getDate(), reservationId));
    }

    private Reservation buildReservation(final ReservationCreationRequest request) {
        return Reservation.builder()
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .customerEmail(request.getCustomerEmail())
                .customerName(request.getCustomerName())
                .status(ReservationStatus.ACTIVE)
                .id(UUID.randomUUID())
                .build();
    }
}
