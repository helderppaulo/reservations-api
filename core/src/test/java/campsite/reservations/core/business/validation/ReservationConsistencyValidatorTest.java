package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.domain.BusinessViolation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class ReservationConsistencyValidatorTest {

    private final ReservationConsistencyValidator validator = new ReservationConsistencyValidator();

    @Test
    public void testViolationIdentifier() {
        Assertions.assertEquals(BusinessViolation.INCONSISTENT_RESERVATION_DATES, validator.violation());
    }

    @Test
    public void testValidation_whenCheckInBeforeCheckOut() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(LocalDate.parse("2021-12-01"))
                .checkOutDate(LocalDate.parse("2021-12-02"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenCheckInEqualsCheckOut() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(LocalDate.parse("2021-12-01"))
                .checkOutDate(LocalDate.parse("2021-12-01"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }

    @Test
    public void testValidation_whenCheckInAfterCheckOut() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder()
                .checkInDate(LocalDate.parse("2021-12-02"))
                .checkOutDate(LocalDate.parse("2021-12-01"))
                .build();

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }
}
