package campsite.reservations.core.repository;

import campsite.reservations.core.domain.ScheduleControl;

import java.util.Optional;
import java.util.UUID;

public interface ScheduleControlRepository {

    Optional<ScheduleControl> retrieveByIdLocking(UUID id);

    ScheduleControl save(ScheduleControl scheduleControl);
}
