package by.nestegg.user.migration.config.properties;

import by.nestegg.user.migration.AbstractTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MigrationDatasourcePropertiesTests extends AbstractTests {

    @Autowired
    private MigrationDatasourceProperties migrationDatasourceProperties;

    @Test
    void testMigrationDatasourceProperties() {
        assertNotNull(migrationDatasourceProperties.getUrl());
        assertNotNull(migrationDatasourceProperties.getUsername());
        assertNotNull(migrationDatasourceProperties.getPassword());
        assertNotNull(migrationDatasourceProperties.getDriverClassName());
    }

}
