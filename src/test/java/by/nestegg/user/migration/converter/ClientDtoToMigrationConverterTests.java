package by.nestegg.user.migration.converter;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.enums.StatusType;
import by.nestegg.user.migration.persistence.Migration;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
class ClientDtoToMigrationConverterTests extends AbstractTests {

    @Autowired
    private ClientDtoToMigrationConverter clientDtoToMigrationConverter;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        public ClientDtoToMigrationConverter clientDtoToMigrationConverter() {
            return new ClientDtoToMigrationConverter();
        }
    }

    @Test
    void testConvert() throws Exception {
        List<ClientDto> clientDtos = getObjectMapper().readValue(new ClassPathResource("client/client_dtos.json")
                .getInputStream(), new TypeReference<>() {
        });

        clientDtos.forEach(clientDto -> {
            Migration migration = clientDtoToMigrationConverter.convert(clientDto);

            assertNotNull(migration);
            assertEquals(clientDto.getExternalId(), migration.getClientExternalId());
            assertEquals(StatusType.SUCCESS, migration.getStatusType());
        });
    }

}
