package campsite.reservations.core.repository;

import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    Reservation saveReservation(Reservation reservation);

    Optional<Reservation> fetchReservation(UUID id);

    Reservation updateReservationStatus(UUID id, ReservationStatus status);

}
