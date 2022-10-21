package by.nestegg.user.migration.converter;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.Constants;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.dto.UserDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
class ClientDtoToUserDtoConverterTests extends AbstractTests {

    @Autowired
    private ClientDtoToUserDtoConverter clientDtoToUserDtoConverter;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        public ClientDtoToUserDtoConverter clientDtoToUserDtoConverter() {
            return new ClientDtoToUserDtoConverter();
        }
    }

    @Test
    void testConvert() throws Exception {
        List<ClientDto> clientDtos = getObjectMapper().readValue(new ClassPathResource("client/client_dtos.json")
                .getInputStream(), new TypeReference<>() {
        });

        clientDtos.forEach(clientDto -> {
            UserDto userDto = clientDtoToUserDtoConverter.convert(clientDto);

            assertNotNull(userDto);
            if (StringUtils.hasLength(clientDto.getNickname())) {
                assertEquals(clientDto.getNickname(), userDto.getDisplayName());
            } else {
                assertEquals(clientDto.getFirstName() + Constants.SPACE + clientDto.getLastName(),
                        userDto.getDisplayName());
            }
            assertEquals(clientDto.getPassword(), userDto.getPassword());
            assertEquals(Constants.EMAIL, userDto.getThreePidsDtos().get(0).getMedium());
            assertEquals(clientDto.getEmail(), userDto.getThreePidsDtos().get(0).getAddress());
            assertFalse(userDto.getIsAdmin());
            assertFalse(userDto.getDeactivated());
        });
    }

}
