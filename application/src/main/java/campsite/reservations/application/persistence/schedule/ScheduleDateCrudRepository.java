package campsite.reservations.application.persistence.schedule;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleDateCrudRepository extends CrudRepository<ScheduleDateEntity, LocalDate> {

    @Query("select s from schedule_date s where (s.reservation is not null and (:reservationId is null or s.reservation.id != :reservationId)) and s.date >= :begin and s.date < :end")
    List<ScheduleDateEntity> retrieveReservedScheduleDates(
            @Param("begin") LocalDate begin,
            @Param("end") LocalDate end,
            @Param("reservationId") UUID ignoredReservationId
    );

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select s from schedule_date s where (s.reservation is null or s.reservation.id = :reservationId) and s.date >= :begin and s.date < :end")
    List<ScheduleDateEntity> retrieveUnreservedScheduleDatesLocking(
            @Param("begin") LocalDate begin,
            @Param("end") LocalDate end,
            @Param("reservationId") UUID ignoredReservationId
    );

    @Query("select s from schedule_date s where s.date >= :begin and s.date <= :end order by s.date")
    List<ScheduleDateEntity> retrieveScheduleDates(@Param("begin") LocalDate begin, @Param("end") LocalDate end);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select s from schedule_date s where s.reservation.id = :reservationId")
    List<ScheduleDateEntity> retrieveScheduleDatesByReservationLocking(@Param("reservationId") UUID reservationId);

}
