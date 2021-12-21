package campsite.reservations.core.business.reservation;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

import static campsite.reservations.core.domain.BusinessViolation.RESERVATION_NOT_FOUND;

@AllArgsConstructor
public class ReservationCancellationService {

    private final ReservationRepository reservationRepository;
    private final ScheduleRepository scheduleRepository;
    private final TransactionProvider transactionProvider;

    public Result<Reservation> cancelReservation(final UUID id) {
        return Result.successful(id)
                .flatMap(this::fetchReservation)
                .map(this::updateReservationAtomically);
    }

    private Reservation updateReservationAtomically(final UUID id) {
        return transactionProvider.executeAtomically(() -> {
            updateSchedule(id);
            return reservationRepository.updateReservationStatus(id, ReservationStatus.CANCELLED);
        });
    }

    private Result<UUID> fetchReservation(final UUID id) {
        final Optional<Reservation> reservation = reservationRepository.fetchReservation(id);
        if (reservation.isEmpty()) return Result.businessError(RESERVATION_NOT_FOUND);
        else return Result.successful(id);
    }

    private void updateSchedule(final UUID reservationId) {
        scheduleRepository.retrieveScheduleDatesByReservationLocking(reservationId)
                .forEach((s) -> scheduleRepository.updateScheduleDateReservation(s.getDate(), null));
    }
}
