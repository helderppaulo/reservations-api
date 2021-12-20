package campsite.reservations.application;

import campsite.reservations.core.ReservationScheduler;
import campsite.reservations.core.ReservationSchedulerFactory;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import campsite.reservations.application.persistence.schedule.ScheduleDateCrudRepository;
import campsite.reservations.application.persistence.schedule.ScheduleDateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Configuration
@EnableSwagger2
@EnableJpaRepositories
public class ApplicationConfiguration {

    @Autowired
    private ScheduleDateCrudRepository scheduleDateCrudRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Bean
    public ReservationScheduler reservationScheduler() {
        return ReservationSchedulerFactory.create(reservationRepository, scheduleRepository, transactionProvider);
    }

    @PostConstruct
    public void initializeSchedule() {
        final LocalDate start = LocalDate.now();
        final LocalDate end = start.plusYears(1);

        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            final ScheduleDateEntity scheduleDate = ScheduleDateEntity.builder().date(date).build();
            scheduleDateCrudRepository.save(scheduleDate);
        }
    }
}
