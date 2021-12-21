package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.business.reservation.ReservationUpdateRequest;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ReservationStatus;
import campsite.reservations.core.repository.ReservationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationStatusValidatorTest {

    @Mock
    private ReservationRepository repository;

    @InjectMocks
    private ReservationStatusValidator validator;

    @Test
    public void testViolationIdentifier() {
        Assertions.assertEquals(BusinessViolation.CANCELLED_RESERVATION, validator.violation());
    }

    @Test
    public void testValidation_whenReservationActive() {
        final UUID id = UUID.randomUUID();
        final Reservation reservation = Reservation.builder()
                .status(ReservationStatus.ACTIVE)
                .build();
        when(repository.fetchReservation(id)).thenReturn(Optional.of(reservation));
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .reservationId(id)
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenNoReservationId() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder().build();

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenCancelledReservation() {
        final UUID id = UUID.randomUUID();
        final Reservation reservation = Reservation.builder()
                .status(ReservationStatus.CANCELLED)
                .build();
        when(repository.fetchReservation(id)).thenReturn(Optional.of(reservation));
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .reservationId(id)
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }
}
