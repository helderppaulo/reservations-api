package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationUpdateRequest;
import campsite.reservations.core.domain.BusinessViolation;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationVacancyValidatorTest {

    @Mock
    private ScheduleRepository repository;

    @InjectMocks
    private ReservationVacancyValidator validator;

    @Test
    public void testViolationIdentifier() {
        Assertions.assertEquals(BusinessViolation.UNAVAILABLE_SELECTED_PERIOD, validator.violation());
    }

    @Test
    public void testValidation_whenValid() {
        final LocalDate checkInDate = LocalDate.parse("2021-12-02");
        final LocalDate checkOutDate = LocalDate.parse("2021-12-04");
        final UUID id = UUID.randomUUID();
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .reservationId(id).build();
        when(repository.retrieveReservedScheduleDates(checkInDate, checkOutDate, id)).thenReturn(List.of());

        final boolean result = validator.validate(request);

        Assertions.assertTrue(result);
    }

    @Test
    public void testValidation_whenDatesUnavailable() {
        final LocalDate checkInDate = LocalDate.parse("2021-12-02");
        final LocalDate checkOutDate = LocalDate.parse("2021-12-04");
        final UUID id = UUID.randomUUID();
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder()
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .reservationId(id).build();
        when(repository.retrieveReservedScheduleDates(checkInDate, checkOutDate, id))
                .thenReturn(List.of(ScheduleDate.builder().build()));

        final boolean result = validator.validate(request);

        Assertions.assertFalse(result);
    }
}
