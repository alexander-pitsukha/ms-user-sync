package by.nestegg.user.migration;

import by.nestegg.user.migration.controller.AppController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class UserMigrationApplicationTests {

    @Autowired
    private AppController appController;

    @Test
    void contextLoads() {
        assertNotNull(appController);
    }

}
