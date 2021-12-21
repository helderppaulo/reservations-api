package campsite.reservations.core.business.reservation;

import campsite.reservations.core.NoopTransactionProvider;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.validation.ReservationValidationExecutor;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static campsite.reservations.core.domain.BusinessViolation.INCONSISTENT_RESERVATION_DATES;
import static campsite.reservations.core.domain.BusinessViolation.INVALID_RESERVATION_REQUEST_PERIOD;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationCreationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ReservationValidationExecutor validator;

    @Spy
    private TransactionProvider transactionProvider = new NoopTransactionProvider();

    @InjectMocks
    private ReservationCreationService reservationCreationService;

    @Test
    public void reservationCreation_whenInvalidRequest() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder().build();
        final List<BusinessViolation> violations = List.of(INCONSISTENT_RESERVATION_DATES, INVALID_RESERVATION_REQUEST_PERIOD);
        when(validator.validate(request)).thenReturn(violations);

        final Result<Reservation> result = reservationCreationService.createReservation(request);

        assertTrue(result.isBusinessError());
        assertEquals(violations, result.unwrapBusinessError());
    }

    @Test
    public void reservationCreation_successfully() {
        final LocalDate checkInDate = LocalDate.parse("2021-12-10");
        final LocalDate checkOutDate = LocalDate.parse("2021-12-12");
        final String customerEmail = "customer@customer.com";
        final String customerName = "customer lastname";
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .customerEmail(customerEmail)
                .customerName(customerName)
                .build();
        when(validator.validate(request)).thenReturn(List.of());
        final LocalDate firstScheduleDate = checkInDate;
        final LocalDate secondScheduleDate = checkInDate.plusDays(1L);
        final UUID persistedReservationId = randomUUID();
        when(reservationRepository.saveReservation(any())).thenReturn(Reservation.builder().id(persistedReservationId).build());
        when(scheduleRepository.retrieveUnreservedScheduleDatesLocking(checkInDate, checkOutDate, null))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(firstScheduleDate).build(),
                        ScheduleDate.builder().date(secondScheduleDate).build()));

        final Result<Reservation> result = reservationCreationService.createReservation(request);

        final ArgumentCaptor<Reservation> reservationArg = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveReservation(reservationArg.capture());
        final Reservation value = reservationArg.getValue();
        assertEquals(checkInDate, value.getCheckInDate());
        assertEquals(checkOutDate, value.getCheckOutDate());
        assertEquals(customerEmail, value.getCustomerEmail());
        assertEquals(customerName, value.getCustomerName());
        assertNotNull(value.getId());
        assertEquals(ReservationStatus.ACTIVE, value.getStatus());
        verify(scheduleRepository).updateScheduleDateReservation(eq(firstScheduleDate), eq(persistedReservationId));
        verify(scheduleRepository).updateScheduleDateReservation(eq(secondScheduleDate), eq(persistedReservationId));
        assertTrue(result.isSuccess());
    }

    @Test
    public void reservationCreation_whenInconsistentScheduleDates() {
        final LocalDate checkInDate = LocalDate.parse("2021-12-10");
        final LocalDate checkOutDate = LocalDate.parse("2021-12-12");
        final String customerEmail = "customer@customer.com";
        final String customerName = "customer lastname";
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .customerEmail(customerEmail)
                .customerName(customerName)
                .build();
        when(validator.validate(request)).thenReturn(List.of());
        final UUID persistedReservationId = randomUUID();
        when(reservationRepository.saveReservation(any())).thenReturn(Reservation.builder().id(persistedReservationId).build());
        when(scheduleRepository.retrieveUnreservedScheduleDatesLocking(checkInDate, checkOutDate, null))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(checkInDate).build()));

        final Result<Reservation> result = reservationCreationService.createReservation(request);

        final ArgumentCaptor<Reservation> reservationArg = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveReservation(reservationArg.capture());
        final Reservation value = reservationArg.getValue();
        assertEquals(checkInDate, value.getCheckInDate());
        assertEquals(checkOutDate, value.getCheckOutDate());
        assertEquals(customerEmail, value.getCustomerEmail());
        assertEquals(customerName, value.getCustomerName());
        assertNotNull(value.getId());
        assertEquals(ReservationStatus.ACTIVE, value.getStatus());

        assertTrue(result.isException());
    }


}
