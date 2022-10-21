package by.nestegg.user.migration.service.impl;

import by.nestegg.user.migration.config.MigrationConfiguration;
import by.nestegg.user.migration.converter.ClientDtoToMigrationConverter;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.enums.MigrationType;
import by.nestegg.user.migration.enums.StatusType;
import by.nestegg.user.migration.persistence.MigrationRepository;
import by.nestegg.user.migration.persistence.SchedulerHistory;
import by.nestegg.user.migration.persistence.SchedulerHistoryRepository;
import by.nestegg.user.migration.service.MigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationServiceImpl implements MigrationService {

    private final MigrationRepository migrationRepository;
    private final SchedulerHistoryRepository schedulerHistoryRepository;
    private final ClientDtoToMigrationConverter clientDtoToMigrationConverter;

    @Override
    @Transactional(value = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER)
    public void saveMigration(final ClientDto clientDto) {
        log.info("Save migration with externalId: {}.", clientDto.getExternalId());
        var migration = Objects.requireNonNull(clientDtoToMigrationConverter.convert(clientDto));
        boolean isExist = migrationRepository.existsByClientExternalId(clientDto.getExternalId());
        migration.setMigrationType(isExist ? MigrationType.UPDATE : MigrationType.ADD);
        migrationRepository.save(migration);
    }

    @Override
    @Transactional(value = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER)
    public Long saveSchedulerHistory() {
        var schedulerHistory = new SchedulerHistory();
        schedulerHistory.setStartedAt(LocalDateTime.now());
        schedulerHistory = schedulerHistoryRepository.save(schedulerHistory);
        return schedulerHistory.getId();
    }

    @Override
    @Transactional(value = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER)
    public void updateSchedulerHistory(final Long schedulerHistoryId, final StatusType statusType) {
        var schedulerHistory = schedulerHistoryRepository.findById(schedulerHistoryId).orElseThrow();
        schedulerHistory.setStatusType(statusType);
        schedulerHistory.setEndedAt(LocalDateTime.now());
    }

    @Override
    @Transactional(value = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER, readOnly = true)
    public LocalDateTime getMaxMigrationDate() {
        return schedulerHistoryRepository.findMaxMigrationDate().orElse(null);
    }

}
