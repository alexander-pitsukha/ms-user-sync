package by.nestegg.user.migration.service;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.converter.ClientDtoToMigrationConverter;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.enums.StatusType;
import by.nestegg.user.migration.persistence.Migration;
import by.nestegg.user.migration.persistence.MigrationRepository;
import by.nestegg.user.migration.persistence.SchedulerHistory;
import by.nestegg.user.migration.persistence.SchedulerHistoryRepository;
import by.nestegg.user.migration.service.impl.MigrationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@MockBean({MigrationRepository.class, SchedulerHistoryRepository.class, ClientDtoToMigrationConverter.class})
class MigrationServiceImplTests extends AbstractTests {

    @Autowired
    private MigrationService migrationService;
    @Autowired
    private MigrationRepository migrationRepository;
    @Autowired
    private SchedulerHistoryRepository schedulerHistoryRepository;
    @Autowired
    private ClientDtoToMigrationConverter clientDtoToMigrationConverter;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        @Autowired
        public MigrationService migrationService(MigrationRepository migrationRepository,
                                                 SchedulerHistoryRepository schedulerHistoryRepository,
                                                 ClientDtoToMigrationConverter clientDtoToMigrationConverter) {
            return new MigrationServiceImpl(migrationRepository, schedulerHistoryRepository,
                    clientDtoToMigrationConverter);
        }
    }

    @Test
    void testSaveMigration() throws Exception {
        ClientDto clientDto = getObjectFromJson("client/client_dto.json", ClientDto.class);
        Migration migration = getObjectFromJson("entity/migration.json", Migration.class);

        when(clientDtoToMigrationConverter.convert(any(ClientDto.class))).thenReturn(migration);
        when(migrationRepository.existsByClientExternalId(any(UUID.class))).thenReturn(true);
        when(migrationRepository.save(any(Migration.class))).thenReturn(migration);

        migrationService.saveMigration(clientDto);

        verify(clientDtoToMigrationConverter).convert(any(ClientDto.class));
        verify(migrationRepository).existsByClientExternalId(any(UUID.class));
        verify(migrationRepository).save(any(Migration.class));
    }

    @Test
    void testSaveSchedulerHistory() throws Exception {
        SchedulerHistory schedulerHistory = getObjectFromJson("entity/scheduler_history_with_id.json",
                SchedulerHistory.class);

        when(schedulerHistoryRepository.save(any(SchedulerHistory.class))).thenReturn(schedulerHistory);

        Long schedulerHistoryId = migrationService.saveSchedulerHistory();

        assertNotNull(schedulerHistoryId);
        assertEquals(schedulerHistory.getId(), schedulerHistoryId);

        verify(schedulerHistoryRepository).save(any(SchedulerHistory.class));
    }

    @Test
    void testUpdateSchedulerHistory() throws Exception {
        SchedulerHistory schedulerHistory = getObjectFromJson("entity/scheduler_history_with_id.json",
                SchedulerHistory.class);

        when(schedulerHistoryRepository.findById(anyLong())).thenReturn(Optional.of(schedulerHistory));

        migrationService.updateSchedulerHistory(schedulerHistory.getId(), StatusType.SUCCESS);

        verify(schedulerHistoryRepository).findById(anyLong());
    }

    @Test
    void testGetMaxMigrationDate() {
        when(schedulerHistoryRepository.findMaxMigrationDate()).thenReturn(Optional.of(LocalDateTime.now()));

        LocalDateTime localDateTime = migrationService.getMaxMigrationDate();

        assertNotNull(localDateTime);

        verify(schedulerHistoryRepository).findMaxMigrationDate();
    }

}
