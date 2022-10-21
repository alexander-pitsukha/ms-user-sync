package by.nestegg.user.migration.service;

import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.enums.StatusType;

import java.time.LocalDateTime;

public interface MigrationService {

    void saveMigration(ClientDto clientDto);

    Long saveSchedulerHistory();

    void updateSchedulerHistory(Long schedulerHistoryId, StatusType statusType);

    LocalDateTime getMaxMigrationDate();

}
