package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.business.reservation.ReservationUpdateRequest;
import campsite.reservations.core.domain.BusinessViolation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static campsite.reservations.core.domain.BusinessViolation.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationValidationExecutorTest {

    @Mock
    private ReservationConsistencyValidator consistencyValidator;

    @Mock
    private ReservationDurationValidator durationValidator;

    @Mock
    private ReservationRequestPeriodValidator periodValidator;

    @Mock
    private ReservationStatusValidator statusValidator;

    @Mock
    private ReservationVacancyValidator vacancyValidator;

    @InjectMocks
    private ReservationValidationExecutor executor;

    @Test
    public void testValidation_whenValidatorsValid() {
        final ReservationRequest request = ReservationUpdateRequest.builder().build();
        mockValidators(true);

        final List<BusinessViolation> result = executor.validate(request);

        assertEquals(List.of(), result);
    }

    @Test
    public void testValidation_whenValidatorsInvalid() {
        final ReservationRequest request = ReservationUpdateRequest.builder().build();
        mockValidators(false);

        final List<BusinessViolation> result = executor.validate(request);

        var expected = List.of(INCONSISTENT_RESERVATION_DATES,
                MAXIMUM_RESERVATION_DURATION_EXCEEDED,
                INVALID_RESERVATION_REQUEST_PERIOD,
                CANCELLED_RESERVATION,
                UNAVAILABLE_SELECTED_PERIOD);
        assertEquals(expected, result);
    }

    private void mockValidators(final boolean valid) {
        when(consistencyValidator.validate(any())).thenReturn(valid);
        when(durationValidator.validate(any())).thenReturn(valid);
        when(periodValidator.validate(any())).thenReturn(valid);
        when(statusValidator.validate(any())).thenReturn(valid);
        when(vacancyValidator.validate(any())).thenReturn(valid);

        if (!valid) {
            when(consistencyValidator.violation()).thenReturn(INCONSISTENT_RESERVATION_DATES);
            when(durationValidator.violation()).thenReturn(MAXIMUM_RESERVATION_DURATION_EXCEEDED);
            when(periodValidator.violation()).thenReturn(INVALID_RESERVATION_REQUEST_PERIOD);
            when(statusValidator.violation()).thenReturn(CANCELLED_RESERVATION);
            when(vacancyValidator.violation()).thenReturn(UNAVAILABLE_SELECTED_PERIOD);
        }
    }
}
