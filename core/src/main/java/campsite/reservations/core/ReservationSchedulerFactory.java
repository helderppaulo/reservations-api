package campsite.reservations.core;

import campsite.reservations.core.business.reservation.ReservationCancellationService;
import campsite.reservations.core.business.reservation.ReservationCreationService;
import campsite.reservations.core.business.reservation.ReservationUpdateService;
import campsite.reservations.core.business.schedule.ScheduleInquiryService;
import campsite.reservations.core.business.validation.ReservationConsistencyValidator;
import campsite.reservations.core.business.validation.ReservationDurationValidator;
import campsite.reservations.core.business.validation.ReservationPeriodValidator;
import campsite.reservations.core.business.validation.ReservationStatusValidator;
import campsite.reservations.core.business.validation.ReservationVacancyValidator;
import campsite.reservations.core.business.validation.ReservationValidationExecutor;
import campsite.reservations.core.repository.ReservationRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;

public class ReservationSchedulerFactory {

    public static ReservationScheduler create(
            final ReservationRepository reservationRepository,
            final ScheduleRepository scheduleRepository,
            final TransactionProvider transactionProvider
    ) {
        final ReservationConsistencyValidator consistencyValidator = new ReservationConsistencyValidator();
        final ReservationDurationValidator durationValidator = new ReservationDurationValidator();
        final ReservationPeriodValidator periodValidator = new ReservationPeriodValidator();
        final ReservationStatusValidator statusValidator = new ReservationStatusValidator(reservationRepository);
        final ReservationVacancyValidator vacancyValidator = new ReservationVacancyValidator(scheduleRepository);
        final ReservationValidationExecutor validator = new ReservationValidationExecutor(consistencyValidator, durationValidator, periodValidator, statusValidator, vacancyValidator);
        final ReservationCreationService creationService = new ReservationCreationService(reservationRepository, scheduleRepository, transactionProvider, validator);
        final ReservationCancellationService cancellationService = new ReservationCancellationService(reservationRepository, scheduleRepository, transactionProvider);
        final ScheduleInquiryService scheduleInquiryService = new ScheduleInquiryService(scheduleRepository);
        final ReservationUpdateService updateService = new ReservationUpdateService(reservationRepository, scheduleRepository, transactionProvider, validator);

        return new ReservationScheduler(creationService, updateService, cancellationService, scheduleInquiryService);
    }
}
