package campsite.reservations.application.persistence.reservation;

import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ReservationDatabaseRepository implements ReservationRepository {

    @Autowired
    private ReservationCrudRepository crudRepository;

    @Override
    public Reservation saveReservation(final Reservation reservation) {
        final ReservationEntity result = crudRepository.save(ReservationMapper.toEntity(reservation));
        return ReservationMapper.fromEntity(result);
    }

    @Override
    public Optional<Reservation> fetchReservation(final UUID id) {
        return crudRepository.findById(id).map(ReservationMapper::fromEntity);
    }

    @Override
    public Reservation updateReservationStatus(final UUID id, final ReservationStatus status) {
        final ReservationEntity entity = crudRepository.findById(id).orElseThrow();
        entity.setStatus(status);
        final ReservationEntity result = crudRepository.save(entity);
        return ReservationMapper.fromEntity(result);
    }
}
