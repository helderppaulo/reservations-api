package campsite.reservations.core;

import campsite.reservations.core.base.Result;
import campsite.reservations.core.business.reservation.ReservationCancellationService;
import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.business.reservation.ReservationCreationService;
import campsite.reservations.core.business.reservation.ReservationUpdateRequest;
import campsite.reservations.core.business.reservation.ReservationUpdateService;
import campsite.reservations.core.business.schedule.ScheduleCreationService;
import campsite.reservations.core.business.schedule.ScheduleInquiryRequest;
import campsite.reservations.core.business.schedule.ScheduleInquiryService;
import campsite.reservations.core.domain.Reservation;
import campsite.reservations.core.domain.ScheduleControl;
import campsite.reservations.core.domain.ScheduleDateAvailability;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ReservationScheduler {

    private final ReservationCreationService reservationCreationService;
    private final ReservationUpdateService reservationUpdateService;
    private final ReservationCancellationService reservationCancellationService;
    private final ScheduleInquiryService scheduleInquiryService;
    private final ScheduleCreationService scheduleCreationService;

    public Result<Reservation> createReservation(final ReservationCreationRequest request) {
        return reservationCreationService.createReservation(request);
    }

    public Result<Reservation> updateReservation(final ReservationUpdateRequest request) {
        return reservationUpdateService.updateReservation(request);
    }

    public Result<List<ScheduleDateAvailability>> fetchSchedule(final ScheduleInquiryRequest request) {
        return scheduleInquiryService.retrieveSchedule(request);
    }

    public Result<Reservation> cancelReservation(final UUID reservationId) {
        return reservationCancellationService.cancelReservation(reservationId);
    }

    public Result<ScheduleControl> createScheduleControl(final UUID controlId) {
        return scheduleCreationService.createControl(controlId);
    }

    public Result<ScheduleControl> createSchedule(final UUID controlId, final LocalDate targetDate, final LocalDate referenceDate) {
        return scheduleCreationService.createSchedule(controlId, targetDate, referenceDate);
    }
}
