package integration;

import campsite.reservations.core.ReservationScheduler;
import campsite.reservations.core.ReservationSchedulerFactory;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleControlRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static integration.MockUtils.mockAvailableSchedule;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ReservationCreationIntegrationTests {

    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
    private final ScheduleControlRepository scheduleControlRepository = mock(ScheduleControlRepository.class);
    private final TransactionProvider transactionProvider = new DummyTransactionProvider();

    private final ReservationScheduler reservationScheduler = ReservationSchedulerFactory
            .create(reservationRepository, scheduleRepository, scheduleControlRepository, transactionProvider);

    @Test
    public void successfulReservation() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .customerName("customer name")
                .customerEmail("customer@customer.com")
                .checkInDate(LocalDate.parse("2021-12-03"))
                .checkOutDate(LocalDate.parse("2021-12-05"))
                .referenceDateTime(LocalDateTime.parse("2021-12-01T00:00:00"))
                .build();
        mockAvailableSchedule(scheduleRepository, LocalDate.parse("2021-12-01"), LocalDate.parse("2022-02-01"));

        final Result<Reservation> result = reservationScheduler.createReservation(request);


    }
}
