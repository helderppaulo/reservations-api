package campsite.reservations.application.persistence.reservation;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ReservationCrudRepository extends CrudRepository<ReservationEntity, UUID> {
}
