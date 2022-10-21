package by.nestegg.user.migration.service;

import by.nestegg.user.migration.AbstractTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@MockBean(UserMigrationService.class)
class ScheduledServiceTests extends AbstractTests {

    @Autowired
    private ScheduledService scheduledService;
    @Autowired
    private UserMigrationService userMigrationService;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        @Autowired
        public ScheduledService scheduledService(UserMigrationService userMigrationService) {
            return new ScheduledService(userMigrationService);
        }
    }

    @Test
    void testStartUsersMigration() {
        doNothing().when(userMigrationService).usersMigration();

        scheduledService.startUsersMigration();

        verify(userMigrationService).usersMigration();
    }

}
