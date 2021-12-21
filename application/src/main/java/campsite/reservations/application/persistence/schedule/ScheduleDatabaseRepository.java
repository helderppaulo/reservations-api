package campsite.reservations.application.persistence.schedule;

import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.application.persistence.reservation.ReservationCrudRepository;
import campsite.reservations.application.persistence.reservation.ReservationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ScheduleDatabaseRepository implements ScheduleRepository {

    @Autowired
    private ScheduleDateCrudRepository scheduleDateCrudRepository;

    @Autowired
    private ReservationCrudRepository reservationCrudRepository;

    @Override
    public List<ScheduleDate> retrieveReservedScheduleDates(final LocalDate begin, final LocalDate end, final UUID ignoredReservationId) {
        return scheduleDateCrudRepository.retrieveReservedScheduleDates(begin, end, ignoredReservationId)
                .stream().map(ScheduleDateMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDate> retrieveUnreservedScheduleDatesLocking(final LocalDate begin, final LocalDate end, final UUID ignoredReservationId) {
        return scheduleDateCrudRepository.retrieveUnreservedScheduleDatesLocking(begin, end, ignoredReservationId)
                .stream().map(ScheduleDateMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDate> retrieveScheduleDatesByReservationLocking(final UUID reservationId) {
        return scheduleDateCrudRepository.retrieveScheduleDatesByReservationLocking(reservationId)
                .stream().map(ScheduleDateMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDate> retrieveScheduleDates(final LocalDate begin, final LocalDate end) {
        return scheduleDateCrudRepository.retrieveScheduleDates(begin, end)
                .stream().map(ScheduleDateMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void updateScheduleDateReservation(final LocalDate date, final UUID reservationId) {
        final ReservationEntity reservation = Optional.ofNullable(reservationId)
                .flatMap(reservationCrudRepository::findById)
                .orElse(null);
        final ScheduleDateEntity scheduleDate = scheduleDateCrudRepository.findById(date).orElseThrow();
        scheduleDate.setReservation(reservation);
        scheduleDateCrudRepository.save(scheduleDate);
    }

    @Override
    public ScheduleDate saveScheduleDate(final ScheduleDate scheduleDate) {
        final ScheduleDateEntity entity = ScheduleDateEntity.builder().date(scheduleDate.getDate()).build();
        final ScheduleDateEntity persistedEntity = scheduleDateCrudRepository.save(entity);
        return ScheduleDateMapper.fromEntity(persistedEntity);
    }
}
