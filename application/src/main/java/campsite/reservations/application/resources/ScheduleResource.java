package campsite.reservations.application.resources;

import campsite.reservations.core.ReservationScheduler;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.schedule.ScheduleInquiryRequest;
import campsite.reservations.core.domain.ScheduleDateAvailability;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@RestController
public class ScheduleResource {

    @Autowired
    private ReservationScheduler reservationScheduler;

    @GetMapping("/schedule-dates")
    public ResponseEntity<ResourceResult<List<ScheduleDateAvailability>>> fetchSchedule(
            @RequestParam(value = "from", required = false) final String from,
            @RequestParam(value = "to", required = false) final String to
    ) {
        final Result<List<ScheduleDateAvailability>> result = Result.fromSupplier(() -> buildRequest(from, to))
                .flatMap((request) -> reservationScheduler.fetchSchedule(request));
        return ResultResponseUnwrapper.unwrap(result);
    }

    private ScheduleInquiryRequest buildRequest(final String from, final String to) {
        return ScheduleInquiryRequest.builder()
                .referenceDateTime(LocalDateTime.now())
                .begin(ofNullable(from).map(LocalDate::parse).orElse(null))
                .end(ofNullable(to).map(LocalDate::parse).orElse(null)).build();
    }
}
