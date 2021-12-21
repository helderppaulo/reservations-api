package campsite.reservations.core;

import campsite.reservations.core.business.reservation.ReservationCancellationService;
import campsite.reservations.core.business.reservation.ReservationCreationService;
import campsite.reservations.core.business.reservation.ReservationUpdateService;
import campsite.reservations.core.business.schedule.ScheduleCreationService;
import campsite.reservations.core.business.schedule.ScheduleInquiryService;
import campsite.reservations.core.business.validation.ReservationConsistencyValidator;
import campsite.reservations.core.business.validation.ReservationDurationValidator;
import campsite.reservations.core.business.validation.ReservationRequestPeriodValidator;
import campsite.reservations.core.business.validation.ReservationStatusValidator;
import campsite.reservations.core.business.validation.ReservationVacancyValidator;
import campsite.reservations.core.business.validation.ReservationValidationExecutor;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleControlRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;

public class ReservationSchedulerFactory {

    public static ReservationScheduler create(
            final ReservationRepository reservationRepository,
            final ScheduleRepository scheduleRepository,
            final ScheduleControlRepository scheduleControlRepository,
            final TransactionProvider transactionProvider
    ) {
        final ReservationValidationExecutor validator = createValidator(reservationRepository, scheduleRepository);
        final ReservationCreationService creationService = new ReservationCreationService(reservationRepository, scheduleRepository, transactionProvider, validator);
        final ReservationCancellationService cancellationService = new ReservationCancellationService(reservationRepository, scheduleRepository, transactionProvider);
        final ReservationUpdateService updateService = new ReservationUpdateService(reservationRepository, scheduleRepository, transactionProvider, validator);

        final ScheduleInquiryService scheduleInquiryService = new ScheduleInquiryService(scheduleRepository);
        final ScheduleCreationService scheduleCreationService = new ScheduleCreationService(scheduleControlRepository, scheduleRepository, transactionProvider);

        return new ReservationScheduler(creationService, updateService, cancellationService, scheduleInquiryService, scheduleCreationService);
    }

    private static ReservationValidationExecutor createValidator(ReservationRepository reservationRepository, ScheduleRepository scheduleRepository) {
        final ReservationConsistencyValidator consistencyValidator = new ReservationConsistencyValidator();
        final ReservationDurationValidator durationValidator = new ReservationDurationValidator();
        final ReservationRequestPeriodValidator periodValidator = new ReservationRequestPeriodValidator();
        final ReservationStatusValidator statusValidator = new ReservationStatusValidator(reservationRepository);
        final ReservationVacancyValidator vacancyValidator = new ReservationVacancyValidator(scheduleRepository);
        return new ReservationValidationExecutor(consistencyValidator, durationValidator, periodValidator, statusValidator, vacancyValidator);
    }
}
