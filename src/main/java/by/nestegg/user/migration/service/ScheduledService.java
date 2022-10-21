package by.nestegg.user.migration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final UserMigrationService userMigrationService;

    @Scheduled(cron = "${user.migration.cron}")
    public void startUsersMigration() {
        userMigrationService.usersMigration();
    }

}
