package by.nestegg.user.migration.service;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.client.Client;
import by.nestegg.user.migration.client.ClientRepository;
import by.nestegg.user.migration.converter.ClientToDtoConverter;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.service.impl.ClientServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@MockBean({ClientRepository.class, ClientToDtoConverter.class})
class ClientServiceImplTests extends AbstractTests {

    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientToDtoConverter clientToDtoConverter;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        @Autowired
        public ClientService clientService(ClientRepository clientRepository,
                                           ClientToDtoConverter clientToDtoConverter) {
            return new ClientServiceImpl(clientRepository, clientToDtoConverter);
        }
    }

    @ParameterizedTest
    @MethodSource
    void testGetClients(LocalDateTime localDateTime) throws Exception {
        List<Client> clients = getObjectMapper().readValue(new ClassPathResource("client/clients.json")
                .getInputStream(), new TypeReference<>() {
        });
        List<ClientDto> clientDtos = getObjectMapper().readValue(new ClassPathResource("client/client_dtos.json")
                .getInputStream(), new TypeReference<>() {
        });

        when(clientRepository.findAllByCreatedAtAndUpdateAt(any(LocalDateTime.class))).thenReturn(clients.stream());
        when(clientRepository.findAll()).thenReturn(clients);
        when(clientToDtoConverter.convert(any(Client.class))).thenReturn(clientDtos.get(0), clientDtos.get(1));

        List<ClientDto> entries = clientService.getClients(localDateTime);

        assertNotNull(entries);
        assertEquals(clients.size(), clientDtos.size());

        verify(clientRepository, times(localDateTime != null ? 1 : 0)).findAllByCreatedAtAndUpdateAt(localDateTime);
        verify(clientRepository, times(localDateTime != null ? 0 : 1)).findAll();
        verify(clientToDtoConverter, times(2)).convert(any(Client.class));
    }

    @Test
    void testGetClient() throws Exception {
        Client client = getObjectFromJson("client/client.json", Client.class);
        ClientDto clientDto = getObjectFromJson("client/client_dto.json", ClientDto.class);

        when(clientRepository.findByExternalId(any(UUID.class))).thenReturn(client);
        when(clientToDtoConverter.convert(any(Client.class))).thenReturn(clientDto);

        ClientDto entry = clientService.getClient(UUID.randomUUID());

        assertNotNull(entry);

        verify(clientRepository).findByExternalId(any(UUID.class));
        verify(clientToDtoConverter).convert(any(Client.class));
    }

    static Stream<LocalDateTime> testGetClients() {
        return Stream.of(LocalDateTime.now(), null);
    }

}
