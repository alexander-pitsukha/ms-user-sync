package by.nestegg.user.migration.repository;

import by.nestegg.user.migration.persistence.SchedulerHistory;
import by.nestegg.user.migration.persistence.SchedulerHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@SqlGroup({
        @Sql(scripts = "classpath:sql/h2/migration_schema.sql"),
        @Sql(scripts = "classpath:sql/h2/migration_drop.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
class SchedulerHistoryRepositoryTests extends AbstractRepositoryTests<SchedulerHistory> {

    @Autowired
    private SchedulerHistoryRepository schedulerHistoryRepository;

    @Test
    void testFindMaxMigrationDate() throws Exception {
        SchedulerHistory schedulerHistory = saveTestEntity("entity/scheduler_history.json",
                SchedulerHistory.class);

        Optional<LocalDateTime> optionalLocalDateTime = schedulerHistoryRepository.findMaxMigrationDate();

        assertTrue(optionalLocalDateTime.isPresent());
        assertEquals(schedulerHistory.getStartedAt(), optionalLocalDateTime.get());
    }

}
