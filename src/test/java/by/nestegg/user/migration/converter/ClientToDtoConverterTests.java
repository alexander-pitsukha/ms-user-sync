package by.nestegg.user.migration.converter;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.client.Client;
import by.nestegg.user.migration.dto.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
class ClientToDtoConverterTests extends AbstractTests {

    @Autowired
    private ClientToDtoConverter clientToDtoConverter;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        public ClientToDtoConverter clientToDtoConverter() {
            return new ClientToDtoConverter();
        }
    }

    @Test
    void testConvert() throws Exception {
        Client client = getObjectFromJson("client/client.json", Client.class);

        ClientDto clientDto = clientToDtoConverter.convert(client);

        assertNotNull(clientDto);
        assertEquals(client.getId(), clientDto.getId());
        assertEquals(client.getNickname(), clientDto.getNickname());
        assertEquals(client.getFirstName(), clientDto.getFirstName());
        assertEquals(client.getLastName(), clientDto.getLastName());
        assertEquals(client.getEmail(), clientDto.getEmail());
        assertEquals(client.getPassword(), clientDto.getPassword());
        assertEquals(client.getProfilePictureUuid(), clientDto.getProfilePictureUuid());
        assertEquals(client.getExternalId(), clientDto.getExternalId());
        assertEquals(client.getCreatedAt(), clientDto.getCreatedAt());
        assertEquals(client.getUpdatedAt(), clientDto.getUpdatedAt());
    }

}
