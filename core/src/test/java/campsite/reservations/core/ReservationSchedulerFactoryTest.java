package campsite.reservations.core;

import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleControlRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class ReservationSchedulerFactoryTest {

    @Test
    public void testFactory() {
        final ReservationRepository reservationRepository = mock(ReservationRepository.class);
        final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
        final ScheduleControlRepository scheduleControlRepository = mock(ScheduleControlRepository.class);
        final TransactionProvider transactionProvider = mock(TransactionProvider.class);

        final ReservationScheduler reservationScheduler = ReservationSchedulerFactory
                .create(reservationRepository, scheduleRepository, scheduleControlRepository, transactionProvider);

        assertNotNull(reservationScheduler);
    }
}
