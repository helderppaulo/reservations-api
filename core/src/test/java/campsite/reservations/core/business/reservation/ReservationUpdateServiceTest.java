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
import java.util.Optional;
import java.util.UUID;

import static campsite.reservations.core.domain.BusinessViolation.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationUpdateServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ReservationValidationExecutor validator;

    @Spy
    private TransactionProvider transactionProvider = new NoopTransactionProvider();

    @InjectMocks
    private ReservationUpdateService reservationUpdateService;

    @Test
    public void reservationUpdate_whenInvalidRequest() {
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder().build();
        final List<BusinessViolation> violations = List.of(INCONSISTENT_RESERVATION_DATES, INVALID_RESERVATION_REQUEST_PERIOD);
        when(validator.validate(request)).thenReturn(violations);

        final Result<Reservation> result = reservationUpdateService.updateReservation(request);

        assertTrue(result.isBusinessError());
        assertEquals(violations, result.unwrapBusinessError());
    }

    @Test
    public void reservationUpdate_whenReservationNotFound() {
        final UUID reservationId = UUID.randomUUID();
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder().reservationId(reservationId).build();
        when(validator.validate(request)).thenReturn(List.of());
        when(reservationRepository.fetchReservation(reservationId)).thenReturn(Optional.empty());

        final Result<Reservation> result = reservationUpdateService.updateReservation(request);

        assertTrue(result.isBusinessError());
        final List<BusinessViolation> violations = result.unwrapBusinessError();
        assertEquals(1, violations.size());
        BusinessViolation violation = violations.stream().findFirst().orElseThrow();
        assertEquals(RESERVATION_NOT_FOUND, violation);
    }

    @Test
    public void reservationUpdate_successfully() {
        final UUID reservationId = UUID.randomUUID();
        final LocalDate currentCheckInDate = LocalDate.parse("2021-12-08");
        final LocalDate currentCheckOutDate = LocalDate.parse("2021-12-10");
        final String customerEmail = "customer@customer.com";
        final String customerName = "customer lastname";
        final Reservation current = Reservation.builder().id(reservationId)
                .customerEmail(customerEmail)
                .customerName(customerName)
                .checkInDate(currentCheckInDate)
                .checkOutDate(currentCheckOutDate)
                .status(ReservationStatus.ACTIVE).build();
        final LocalDate checkInDate = LocalDate.parse("2021-12-10");
        final LocalDate checkOutDate = LocalDate.parse("2021-12-12");
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .reservationId(reservationId)
                .checkOutDate(checkOutDate)
                .checkInDate(checkInDate)
                .build();
        final LocalDate firstCurrentScheduleDate = LocalDate.parse("2021-12-08");
        final LocalDate secondCurrentScheduleDate = LocalDate.parse("2021-12-09");
        when(validator.validate(request)).thenReturn(List.of());
        when(reservationRepository.fetchReservation(reservationId)).thenReturn(Optional.of(current));
        when(scheduleRepository.retrieveScheduleDatesByReservationLocking(reservationId))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(firstCurrentScheduleDate).reservation(current).build(),
                        ScheduleDate.builder().date(secondCurrentScheduleDate).reservation(current).build()));
        final LocalDate firstScheduleDate = LocalDate.parse("2021-12-10");
        final LocalDate secondScheduleDate = LocalDate.parse("2021-12-11");
        when(scheduleRepository.retrieveUnreservedScheduleDatesLocking(checkInDate, checkOutDate, reservationId))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(firstScheduleDate).build(),
                        ScheduleDate.builder().date(secondScheduleDate).build()));

        final Result<Reservation> result = reservationUpdateService.updateReservation(request);

        verify(scheduleRepository).updateScheduleDateReservation(eq(firstCurrentScheduleDate), isNull());
        verify(scheduleRepository).updateScheduleDateReservation(eq(secondCurrentScheduleDate), isNull());
        verify(scheduleRepository).updateScheduleDateReservation(eq(firstScheduleDate), eq(reservationId));
        verify(scheduleRepository).updateScheduleDateReservation(eq(secondScheduleDate), eq(reservationId));
        final ArgumentCaptor<Reservation> reservationArg = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).saveReservation(reservationArg.capture());
        final Reservation value = reservationArg.getValue();
        assertEquals(checkInDate, value.getCheckInDate());
        assertEquals(checkOutDate, value.getCheckOutDate());
        assertEquals(customerEmail, value.getCustomerEmail());
        assertEquals(customerName, value.getCustomerName());
        assertNotNull(value.getId());
        assertEquals(ReservationStatus.ACTIVE, value.getStatus());
        assertTrue(result.isSuccess());
    }

    @Test
    public void reservationUpdate_whenInconsistentScheduleDates() {
        final UUID reservationId = UUID.randomUUID();
        final LocalDate currentCheckInDate = LocalDate.parse("2021-12-08");
        final LocalDate currentCheckOutDate = LocalDate.parse("2021-12-10");
        final String customerEmail = "customer@customer.com";
        final String customerName = "customer lastname";
        final Reservation current = Reservation.builder().id(reservationId)
                .customerEmail(customerEmail)
                .customerName(customerName)
                .checkInDate(currentCheckInDate)
                .checkOutDate(currentCheckOutDate)
                .status(ReservationStatus.ACTIVE).build();
        final LocalDate checkInDate = LocalDate.parse("2021-12-10");
        final LocalDate checkOutDate = LocalDate.parse("2021-12-12");
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .reservationId(reservationId)
                .checkOutDate(checkOutDate)
                .checkInDate(checkInDate)
                .build();
        final LocalDate firstCurrentScheduleDate = LocalDate.parse("2021-12-08");
        final LocalDate secondCurrentScheduleDate = LocalDate.parse("2021-12-09");
        when(validator.validate(request)).thenReturn(List.of());
        when(reservationRepository.fetchReservation(reservationId)).thenReturn(Optional.of(current));
        when(scheduleRepository.retrieveScheduleDatesByReservationLocking(reservationId))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(firstCurrentScheduleDate).reservation(current).build(),
                        ScheduleDate.builder().date(secondCurrentScheduleDate).reservation(current).build()));
        final LocalDate firstScheduleDate = LocalDate.parse("2021-12-10");
        when(scheduleRepository.retrieveUnreservedScheduleDatesLocking(checkInDate, checkOutDate, reservationId))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(firstScheduleDate).build()));

        final Result<Reservation> result = reservationUpdateService.updateReservation(request);

        verify(scheduleRepository).updateScheduleDateReservation(eq(firstCurrentScheduleDate), isNull());
        verify(scheduleRepository).updateScheduleDateReservation(eq(secondCurrentScheduleDate), isNull());
        assertTrue(result.isException());
    }

}
