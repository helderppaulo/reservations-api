package campsite.reservations.application.persistence.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "schedule_control")
@Table(name = "SCHEDULE_CONTROL")
public class ScheduleControlEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "last_schedule_date")
    private LocalDate lastScheduleDate;

    @Column(name = "last_execution")
    private LocalDate lastExecution;
}
