package campsite.reservations.core.repository;

import campsite.reservations.core.domain.ScheduleDate;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository {

    List<ScheduleDate> retrieveReservedScheduleDates(LocalDate begin, LocalDate end, UUID ignoredReservationId);

    List<ScheduleDate> retrieveUnreservedScheduleDatesLocking(LocalDate begin, LocalDate end, UUID ignoredReservationId);

    List<ScheduleDate> retrieveScheduleDatesByReservationLocking(UUID reservationId);

    List<ScheduleDate> retrieveScheduleDates(LocalDate begin, LocalDate end);

    void updateScheduleDateReservation(LocalDate date, UUID reservationId);

    ScheduleDate saveScheduleDate(ScheduleDate date);
}
