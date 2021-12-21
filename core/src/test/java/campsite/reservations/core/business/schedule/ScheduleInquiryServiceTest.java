package campsite.reservations.core.business.schedule;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.domain.ScheduleDateAvailability;
import campsite.reservations.core.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleInquiryServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleInquiryService scheduleInquiryService;

    @Test
    public void testScheduleRetrieval_successfully() {
        final LocalDate begin = LocalDate.parse("2021-12-01");
        final LocalDate end = LocalDate.parse("2021-12-02");
        final Reservation someReservation = Reservation.builder().build();
        final ScheduleInquiryRequest request = ScheduleInquiryRequest.builder()
                .begin(begin)
                .end(end)
                .referenceDateTime(LocalDateTime.now()).build();
        when(scheduleRepository.retrieveScheduleDates(begin, end))
                .thenReturn(List.of(
                        ScheduleDate.builder().date(begin).build(),
                        ScheduleDate.builder().date(end).reservation(someReservation).build()));

        Result<List<ScheduleDateAvailability>> result = scheduleInquiryService.retrieveSchedule(request);

        assertTrue(result.isSuccess());
        final List<ScheduleDateAvailability> schedule = result.unwrap(Function.identity());
        assertEquals(2, schedule.size());
        final ScheduleDateAvailability first = schedule.get(0);
        assertEquals(begin, first.getDate());
        assertTrue(first.isAvailable());
        final ScheduleDateAvailability second = schedule.get(1);
        assertEquals(end, second.getDate());
        assertFalse(second.isAvailable());
    }

    @Test
    public void testScheduleRetrieval_whenNoIntervalProvided() {
        final LocalDateTime referenceDate = LocalDateTime.parse("2021-12-01T10:00:00");
        final ScheduleInquiryRequest request = ScheduleInquiryRequest.builder()
                .referenceDateTime(referenceDate).build();

        scheduleInquiryService.retrieveSchedule(request);

        verify(scheduleRepository).retrieveScheduleDates(eq(LocalDate.parse("2021-12-01")), eq(LocalDate.parse("2022-01-01")));
    }
}
