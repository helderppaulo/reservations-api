package campsite.reservations.core.business.schedule;

import campsite.reservations.core.NoopTransactionProvider;
import campsite.reservations.core.base.Result;
import campsite.reservations.core.domain.ScheduleControl;
import campsite.reservations.core.domain.ScheduleDate;
import campsite.reservations.core.repository.ScheduleControlRepository;
import campsite.reservations.core.repository.ScheduleRepository;
import campsite.reservations.core.repository.TransactionProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleCreationServiceTest {

    @Mock
    private ScheduleControlRepository scheduleControlRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Spy
    private TransactionProvider transactionProvider = new NoopTransactionProvider();

    @InjectMocks
    private ScheduleCreationService scheduleCreationService;

    @Test
    public void testControlCreation_successfully() {
        final UUID id = UUID.randomUUID();
        when(scheduleControlRepository.retrieveByIdLocking(id)).thenReturn(Optional.empty());

        final Result<ScheduleControl> result = scheduleCreationService.createControl(id);

        final ArgumentCaptor<ScheduleControl> scheduleControlArg = ArgumentCaptor.forClass(ScheduleControl.class);
        verify(scheduleControlRepository).save(scheduleControlArg.capture());
        final ScheduleControl scheduleControl = scheduleControlArg.getValue();
        assertNull(scheduleControl.getLastExecution());
        assertNull(scheduleControl.getLastScheduleDate());
        assertEquals(id, scheduleControl.getId());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testControlCreation_whenAlreadyExists() {
        final UUID id = UUID.randomUUID();
        final ScheduleControl scheduleControl = ScheduleControl.builder().id(id).build();
        when(scheduleControlRepository.retrieveByIdLocking(id)).thenReturn(Optional.of(scheduleControl));

        final Result<ScheduleControl> result = scheduleCreationService.createControl(id);

        verifyNoMoreInteractions(scheduleControlRepository);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testScheduleCreation_successfully() {
        final UUID id = UUID.randomUUID();
        final LocalDate referenceDate = LocalDate.parse("2021-12-01");
        final LocalDate lastScheduleDate = LocalDate.parse("2021-12-02");
        final LocalDate targetDate = LocalDate.parse("2021-12-04");
        final ScheduleControl scheduleControl = ScheduleControl.builder()
                .lastScheduleDate(lastScheduleDate)
                .id(id).build();
        when(scheduleControlRepository.retrieveByIdLocking(id)).thenReturn(Optional.of(scheduleControl));

        final Result<ScheduleControl> result = scheduleCreationService.createSchedule(id, targetDate, referenceDate);

        assertTrue(result.isSuccess());
        final ArgumentCaptor<ScheduleDate> captor = ArgumentCaptor.forClass(ScheduleDate.class);
        verify(scheduleRepository, times(2)).saveScheduleDate(captor.capture());
        final List<ScheduleDate> dates = captor.getAllValues();
        assertEquals(2, dates.size());
        final ScheduleDate first = dates.get(0);
        assertEquals(LocalDate.parse("2021-12-02"), first.getDate());
        final ScheduleDate second = dates.get(1);
        assertEquals(LocalDate.parse("2021-12-03"), second.getDate());
        verifyNoMoreInteractions(scheduleRepository);
        final ArgumentCaptor<ScheduleControl> controlCaptor = ArgumentCaptor.forClass(ScheduleControl.class);
        verify(scheduleControlRepository).save(controlCaptor.capture());
        final ScheduleControl updatedControl = controlCaptor.getValue();
        assertEquals(referenceDate, updatedControl.getLastExecution());
        assertEquals(id, updatedControl.getId());
        assertEquals(LocalDate.parse("2021-12-04"), updatedControl.getLastScheduleDate());
    }

    @Test
    public void testScheduleCreation_whenNoScheduleDateCreated() {
        final UUID id = UUID.randomUUID();
        final LocalDate referenceDate = LocalDate.parse("2021-12-01");
        final LocalDate targetDate = LocalDate.parse("2021-12-04");
        final ScheduleControl scheduleControl = ScheduleControl.builder()
                .id(id).build();
        when(scheduleControlRepository.retrieveByIdLocking(id)).thenReturn(Optional.of(scheduleControl));

        final Result<ScheduleControl> result = scheduleCreationService.createSchedule(id, targetDate, referenceDate);

        assertTrue(result.isSuccess());
        final ArgumentCaptor<ScheduleDate> captor = ArgumentCaptor.forClass(ScheduleDate.class);
        verify(scheduleRepository, times(3)).saveScheduleDate(captor.capture());
        final List<ScheduleDate> dates = captor.getAllValues();
        assertEquals(3, dates.size());
        final ScheduleDate first = dates.get(0);
        assertEquals(LocalDate.parse("2021-12-01"), first.getDate());
        final ScheduleDate second = dates.get(1);
        assertEquals(LocalDate.parse("2021-12-02"), second.getDate());
        final ScheduleDate third = dates.get(2);
        assertEquals(LocalDate.parse("2021-12-03"), third.getDate());
        verifyNoMoreInteractions(scheduleRepository);
        final ArgumentCaptor<ScheduleControl> controlCaptor = ArgumentCaptor.forClass(ScheduleControl.class);
        verify(scheduleControlRepository).save(controlCaptor.capture());
        final ScheduleControl updatedControl = controlCaptor.getValue();
        assertEquals(referenceDate, updatedControl.getLastExecution());
        assertEquals(id, updatedControl.getId());
        assertEquals(LocalDate.parse("2021-12-04"), updatedControl.getLastScheduleDate());
    }
}
