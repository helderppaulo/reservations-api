package campsite.reservations.application;

import campsite.reservations.core.ReservationScheduler;
import campsite.reservations.core.ReservationSchedulerFactory;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleControlRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableJpaRepositories
public class ApplicationConfiguration {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private TransactionProvider transactionProvider;

    @Autowired
    private ScheduleControlRepository scheduleControlRepository;

    @Bean
    public ReservationScheduler reservationScheduler() {
        return ReservationSchedulerFactory.create(reservationRepository, scheduleRepository, scheduleControlRepository, transactionProvider);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(this.getClass().getPackageName()))
                .paths(PathSelectors.any())
                .build();
    }
}
