package campsite.reservations.application.schedulers;

import campsite.reservations.core.ReservationScheduler;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.ScheduleControl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
public class ScheduleCreationJob {

    @Value("${schedule.control.id}")
    private UUID scheduleControlId;

    @Autowired
    private ReservationScheduler reservationScheduler;

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void createScheduleJob() {
        createSchedule();
    }

    @PostConstruct
    public void initializeScheduleControl() {
        reservationScheduler.createScheduleControl(scheduleControlId);
        createSchedule();
    }

    private void createSchedule() {
        LocalDate currentDate = LocalDate.now();
        log.info("creating schedule");
        Result<ScheduleControl> result = reservationScheduler.createSchedule(scheduleControlId, currentDate.plusMonths(3L), currentDate);
        if (result.isException()) {
            log.error("result=failure", result.unwrapException());
        }
    }
}
