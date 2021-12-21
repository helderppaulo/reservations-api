package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.domain.BusinessViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReservationRequestPeriodValidatorTest {

    private final ReservationRequestPeriodValidator validator = new ReservationRequestPeriodValidator();

    @Test
    public void testViolationIdentifier() {
        Assertions.assertEquals(BusinessViolation.INVALID_RESERVATION_REQUEST_PERIOD, validator.violation());
    }

    @Test
    public void testValidation_whenValid() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .referenceDateTime(LocalDateTime.parse("2021-12-01T10:00:00"))
                .checkInDate(LocalDate.parse("2021-12-02"))
                .checkOutDate(LocalDate.parse("2021-12-04"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenSameDateReservation() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .referenceDateTime(LocalDateTime.parse("2021-12-01T10:00:00"))
                .checkInDate(LocalDate.parse("2021-12-01"))
                .checkOutDate(LocalDate.parse("2021-12-04"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }

    @Test
    public void testValidation_whenBeyondMaximum() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .referenceDateTime(LocalDateTime.parse("2021-12-01T10:00:00"))
                .checkInDate(LocalDate.parse("2022-01-10"))
                .checkOutDate(LocalDate.parse("2022-01-12"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }

    @Test
    public void testValidation_whenPastReservation() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .referenceDateTime(LocalDateTime.parse("2021-12-01T10:00:00"))
                .checkInDate(LocalDate.parse("2021-11-28"))
                .checkOutDate(LocalDate.parse("2021-12-01"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }
}
