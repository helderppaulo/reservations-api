package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;

import java.util.List;
import java.util.stream.Collectors;

public class ReservationValidationExecutor {

    private final List<ReservationValidator> validators;

    public ReservationValidationExecutor(
            final ReservationConsistencyValidator consistencyValidator,
            final ReservationDurationValidator durationValidator,
            final ReservationRequestPeriodValidator periodValidator,
            final ReservationStatusValidator statusValidator,
            final ReservationVacancyValidator vacancyValidator
    ) {
        this.validators = List.of(consistencyValidator, durationValidator, periodValidator, statusValidator, vacancyValidator);
    }

    public List<BusinessViolation> validate(final ReservationRequest request) {
        return validators.stream()
                .filter(it -> !it.validate(request))
                .map(ReservationValidator::violation)
                .collect(Collectors.toUnmodifiableList());
    }
}
