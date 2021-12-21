package campsite.reservations.core.business.schedule;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.ScheduleControl;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ScheduleControlRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static campsite.reservations.core.base.Result.fromSupplier;

@AllArgsConstructor
public class ScheduleCreationService {

    private static final int CREATION_STEP_DAYS = 1;

    private final ScheduleControlRepository scheduleControlRepository;
    private final ScheduleRepository scheduleRepository;
    private final TransactionProvider transactionProvider;

    public Result<ScheduleControl> createControl(final UUID id) {
        return fromSupplier(() -> {
            final Optional<ScheduleControl> control = scheduleControlRepository.retrieveByIdLocking(id);
            if (control.isEmpty()) {
                return scheduleControlRepository.save(ScheduleControl.builder().id(id).build());
            } else {
                return control.get();
            }
        });
    }

    public Result<ScheduleControl> createSchedule(final UUID id, final LocalDate targetScheduleDate, final LocalDate referenceDate) {
        return fromSupplier(() -> transactionProvider.executeAtomically(() -> createScheduleDates(id, targetScheduleDate, referenceDate)));
    }

    private ScheduleControl createScheduleDates(final UUID id, final LocalDate targetScheduleDate, final LocalDate referenceDate) {
        final Optional<ScheduleControl> controlOptional = scheduleControlRepository.retrieveByIdLocking(id);
        if (controlOptional.isPresent()) {
            final ScheduleControl control = controlOptional.get();
            final LocalDate start = Optional.ofNullable(control.getLastScheduleDate()).orElse(referenceDate);
            LocalDate date;
            for (date = start; date.isBefore(targetScheduleDate); date = date.plusDays(CREATION_STEP_DAYS)) {
                scheduleRepository.saveScheduleDate(ScheduleDate.builder().date(date).build());
            }
            final ScheduleControl updatedControl = ScheduleControl.builder()
                    .lastScheduleDate(date)
                    .lastExecution(referenceDate)
                    .id(id).build();
            return scheduleControlRepository.save(updatedControl);
        } else {
            throw new RuntimeException("schedule control not found");
        }
    }

}
