package by.nestegg.user.migration.repository;

import by.nestegg.user.migration.persistence.Migration;
import by.nestegg.user.migration.persistence.MigrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@SqlGroup({
        @Sql(scripts = "classpath:sql/h2/migration_schema.sql"),
        @Sql(scripts = "classpath:sql/h2/migration_drop.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
class MigrationRepositoryTests extends AbstractRepositoryTests<Migration> {

    @Autowired
    private MigrationRepository migrationRepository;

    @Test
    void testExistsByClientExternalId() throws Exception {
        Migration migration = saveTestEntity("entity/migration.json", Migration.class);

        boolean result = migrationRepository.existsByClientExternalId(migration.getClientExternalId());

        assertTrue(result);
    }

}
