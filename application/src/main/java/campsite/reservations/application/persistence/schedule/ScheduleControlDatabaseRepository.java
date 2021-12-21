package campsite.reservations.application.persistence.schedule;

import campsite.reservations.core.domain.ScheduleControl;
import campsite.reservations.core.repository.ScheduleControlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class ScheduleControlDatabaseRepository implements ScheduleControlRepository {

    @Autowired
    private ScheduleControlCrudRepository crudRepository;

    @Override
    public Optional<ScheduleControl> retrieveByIdLocking(final UUID id) {
        return crudRepository.findById(id).map(ScheduleControlMapper::fromEntity);
    }

    @Override
    public ScheduleControl save(final ScheduleControl scheduleControl) {
        final ScheduleControlEntity entity = crudRepository.save(ScheduleControlMapper.toEntity(scheduleControl));
        return ScheduleControlMapper.fromEntity(entity);
    }
}
