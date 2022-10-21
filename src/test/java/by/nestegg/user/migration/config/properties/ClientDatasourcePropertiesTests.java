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
class ClientDatasourcePropertiesTests extends AbstractTests {

    @Autowired
    private ClientDatasourceProperties clientDatasourceProperties;

    @Test
    void testClientDatasourceProperties() {
        assertNotNull(clientDatasourceProperties.getUrl());
        assertNotNull(clientDatasourceProperties.getUsername());
        assertNotNull(clientDatasourceProperties.getPassword());
        assertNotNull(clientDatasourceProperties.getDriverClassName());
    }

}
