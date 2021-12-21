package campsite.reservations.core;

import campsite.reservations.core.business.reservation.ReservationCancellationService;
import campsite.reservations.core.business.reservation.ReservationCreationRequest;
import campsite.reservations.core.business.reservation.ReservationCreationService;
import campsite.reservations.core.business.reservation.ReservationUpdateRequest;
import campsite.reservations.core.business.reservation.ReservationUpdateService;
import campsite.reservations.core.business.schedule.ScheduleCreationService;
import campsite.reservations.core.business.schedule.ScheduleInquiryRequest;
import campsite.reservations.core.business.schedule.ScheduleInquiryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReservationSchedulerTest {

    @Mock
    private ReservationCreationService reservationCreationService;

    @Mock
    private ReservationUpdateService reservationUpdateService;

    @Mock
    private ReservationCancellationService reservationCancellationService;

    @Mock
    private ScheduleInquiryService scheduleInquiryService;

    @Mock
    private ScheduleCreationService scheduleCreationService;

    @InjectMocks
    private ReservationScheduler reservationScheduler;

    @Test
    public void createReservation() {
        final ReservationCreationRequest request = ReservationCreationRequest.builder().build();

        reservationScheduler.createReservation(request);

        verify(reservationCreationService).createReservation(same(request));
    }

    @Test
    public void updateReservation() {
        final ReservationUpdateRequest request = ReservationUpdateRequest.builder().build();

        reservationScheduler.updateReservation(request);

        verify(reservationUpdateService).updateReservation(same(request));
    }

    @Test
    public void fetchSchedule() {
        final ScheduleInquiryRequest request = ScheduleInquiryRequest.builder().build();

        reservationScheduler.fetchSchedule(request);

        verify(scheduleInquiryService).retrieveSchedule(same(request));
    }

    @Test
    public void cancelReservation() {
        final UUID id = UUID.randomUUID();

        reservationScheduler.cancelReservation(id);

        verify(reservationCancellationService).cancelReservation(same(id));
    }

    @Test
    public void createScheduleControl() {
        final UUID id = UUID.randomUUID();

        reservationScheduler.createScheduleControl(id);

        verify(scheduleCreationService).createControl(same(id));
    }

    @Test
    public void createSchedule() {
        final UUID id = UUID.randomUUID();
        final LocalDate target = LocalDate.parse("2022-03-01");
        final LocalDate reference = LocalDate.parse("2021-12-01");

        reservationScheduler.createSchedule(id, target, reference);

        verify(scheduleCreationService).createSchedule(same(id), same(target), same(reference));
    }
}
