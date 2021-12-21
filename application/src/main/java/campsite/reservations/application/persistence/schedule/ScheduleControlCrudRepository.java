package campsite.reservations.application.persistence.schedule;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleControlCrudRepository extends CrudRepository<ScheduleControlEntity, UUID> {

    @Override
    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "0")})
    Optional<ScheduleControlEntity> findById(UUID uuid);
}
