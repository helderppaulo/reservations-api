package campsite.reservations.core.business.validation;

import campsite.reservations.core.business.reservation.ReservationRequest;
import campsite.reservations.core.domain.BusinessViolation;

public interface ReservationValidator {

    boolean validate(ReservationRequest request);

    BusinessViolation violation();
}
