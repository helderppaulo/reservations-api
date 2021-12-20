package campsite.reservations.core.business.schedule;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.domain.ScheduleDateAvailability;
import campsite.reservations.core.repository.ScheduleRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.MONTHS;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;

@AllArgsConstructor
public class ScheduleInquiryService {

    private static final int DEFAULT_INTERVAL_MONTHS = 1;

    private final ScheduleRepository scheduleRepository;

    public Result<List<ScheduleDateAvailability>> retrieveSchedule(final ScheduleInquiryRequest request) {
        final LocalDate referenceDate = request.getReferenceDateTime().toLocalDate();
        final LocalDate begin = ofNullable(request.getBegin()).orElse(referenceDate);
        final LocalDate end = ofNullable(request.getEnd()).orElse(begin.plus(DEFAULT_INTERVAL_MONTHS, MONTHS));
        return Result.fromSupplier(() -> scheduleRepository.retrieveScheduleDates(begin, end)
                .stream().map(this::buildAvailability)
                .collect(toUnmodifiableList()));
    }

    private ScheduleDateAvailability buildAvailability(final ScheduleDate scheduleDate) {
        return ScheduleDateAvailability.builder()
                .date(scheduleDate.getDate())
                .available(scheduleDate.getReservation() == null)
                .build();
    }
}
