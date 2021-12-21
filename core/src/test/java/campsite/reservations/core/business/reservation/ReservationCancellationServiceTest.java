package campsite.reservations.core.business.reservation;

import campsite.reservations.core.NoopTransactionProvider;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static campsite.reservations.core.domain.BusinessViolation.RESERVATION_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationCancellationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Spy
    private TransactionProvider transactionProvider = new NoopTransactionProvider();

    @InjectMocks
    private ReservationCancellationService reservationCancellationService;

    @Test
    public void cancellation_whenReservationNotFound() {
        final UUID id = UUID.randomUUID();
        when(reservationRepository.fetchReservation(id)).thenReturn(Optional.empty());

        final Result<Reservation> result = reservationCancellationService.cancelReservation(id);

        assertTrue(result.isBusinessError());
        final List<BusinessViolation> violations = result.unwrapBusinessError();
        assertEquals(1, violations.size());
        BusinessViolation violation = violations.stream().findFirst().orElseThrow();
        assertEquals(RESERVATION_NOT_FOUND, violation);
    }

    @Test
    public void cancellation_successfully() {
        final UUID id = UUID.randomUUID();
        final Reservation reservation = Reservation.builder().build();
        when(reservationRepository.fetchReservation(id)).thenReturn(Optional.of(reservation));
        final LocalDate firstDate = LocalDate.parse("2021-12-01");
        final LocalDate secondDate = LocalDate.parse("2021-12-02");
        when(scheduleRepository.retrieveScheduleDatesByReservationLocking(id))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(firstDate).build(),
                        ScheduleDate.builder().date(secondDate).build()));

        final Result<Reservation> result = reservationCancellationService.cancelReservation(id);

        assertTrue(result.isSuccess());
        verify(scheduleRepository).updateScheduleDateReservation(eq(firstDate), isNull());
        verify(scheduleRepository).updateScheduleDateReservation(eq(secondDate), isNull());
        verifyNoMoreInteractions(scheduleRepository);
        verify(reservationRepository).updateReservationStatus(eq(id), eq(ReservationStatus.CANCELLED));
    }
}
