package campsite.reservations.core.business.reservation;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.validation.ReservationValidationExecutor;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ReservationUpdateService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final TransactionProvider transactionProvider;
    private final ReservationValidationExecutor validator;

    public Result<Reservation> updateReservation(final ReservationUpdateRequest request) {
        return Result.successful(request)
                .flatMap(this::validate)
                .flatMap(this::updateReservationAtomically);
    }

    private Result<ReservationUpdateRequest> validate(final ReservationUpdateRequest request) {
        final List<BusinessViolation> violations = this.validator.validate(request);
        if (violations.isEmpty()) return Result.successful(request);
        else return Result.businessError(violations);
    }

    private Result<Reservation> updateReservationAtomically(final ReservationUpdateRequest request) {
        final Optional<Reservation> reservation = reservationRepository.fetchReservation(request.getReservationId());
        if (reservation.isEmpty()) return Result.businessError(BusinessViolation.RESERVATION_NOT_FOUND);
        return Result.fromSupplier(() -> transactionProvider.executeAtomically(() -> {
            final Reservation current = reservation.get();
            clearPreviousSchedule(request.getReservationId());
            updateSchedule(request.getReservationId(), request.getCheckInDate(), request.getCheckOutDate());
            return updateReservationInterval(request, current);
        }));
    }

    private void clearPreviousSchedule(final UUID reservationId) {
        final List<ScheduleDate> dates = scheduleRepository.retrieveScheduleDatesByReservationLocking(reservationId);
        dates.forEach(date -> scheduleRepository.updateScheduleDateReservation(date.getDate(), null));
    }

    private void updateSchedule(final UUID reservationId, final LocalDate begin, final LocalDate end) {
        final List<ScheduleDate> dates = scheduleRepository.retrieveUnreservedScheduleDatesLocking(begin, end, reservationId);
        checkScheduleVacancy(dates, begin, end);
        dates.forEach(date -> scheduleRepository.updateScheduleDateReservation(date.getDate(), reservationId));
    }

    private void checkScheduleVacancy(final List<ScheduleDate> dates, final LocalDate begin, final LocalDate end) {
        final long days = ChronoUnit.DAYS.between(begin, end);
        if (days != dates.size()) throw new RuntimeException("inconsistent schedule");
    }

    private Reservation updateReservationInterval(final ReservationUpdateRequest request, final Reservation reservation) {
        return reservationRepository.saveReservation(buildReservation(reservation, request));
    }

    private Reservation buildReservation(final Reservation current, final ReservationUpdateRequest request) {
        return Reservation.builder()
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .customerEmail(current.getCustomerEmail())
                .customerName(current.getCustomerName())
                .status(current.getStatus())
                .id(current.getId())
                .build();
    }
}
