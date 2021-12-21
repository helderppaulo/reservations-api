package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.domain.BusinessViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ReservationDurationValidatorTest {

    private final ReservationDurationValidator validator = new ReservationDurationValidator();

    @Test
    public void testViolationIdentifier() {
        Assertions.assertEquals(BusinessViolation.MAXIMUM_RESERVATION_DURATION_EXCEEDED, validator.violation());
    }

    @Test
    public void testValidation_whenValid() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(LocalDate.parse("2021-12-01"))
                .checkOutDate(LocalDate.parse("2021-12-02"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenZeroDayReservation() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(LocalDate.parse("2021-12-01"))
                .checkOutDate(LocalDate.parse("2021-12-01"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenLongerThanMaximum() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(LocalDate.parse("2021-12-01"))
                .checkOutDate(LocalDate.parse("2022-12-01"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }
}
