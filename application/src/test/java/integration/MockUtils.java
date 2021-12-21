package integration;

import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public final class MockUtils {

    public static void mockAvailableSchedule(final ScheduleRepository mock, final LocalDate begin, final LocalDate end) {
        List<ScheduleDate> result = new ArrayList<>();
        for (LocalDate date = begin; date.isBefore(end); date = date.plusDays(1)) {
            result.add(ScheduleDate.builder().date(date).build());
        }
        when(mock.retrieveReservedScheduleDates(begin, end, null))
                .thenReturn(result);
    }
}
