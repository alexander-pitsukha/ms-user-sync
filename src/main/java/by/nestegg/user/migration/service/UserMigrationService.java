package by.nestegg.user.migration.service;

import java.util.UUID;

public interface UserMigrationService {

    void usersMigration();

    void userMigration(UUID externalId);

}
