package by.nestegg.user.migration.service;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.converter.ClientDtoToUserDtoConverter;
import by.nestegg.user.migration.dto.AuthResponseDto;
import by.nestegg.user.migration.dto.AvatarResponseDto;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.dto.UserDto;
import by.nestegg.user.migration.service.impl.UserMigrationServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@MockBean({ClientService.class, MigrationService.class, ClientDtoToUserDtoConverter.class, RestTemplate.class,
        ThreadPoolTaskExecutor.class})
class UserMigrationServiceImplTests extends AbstractTests {

    @Autowired
    private UserMigrationService userMigrationService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private MigrationService migrationService;
    @Autowired
    private ClientDtoToUserDtoConverter clientDtoToUserDtoConverter;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @TestConfiguration
    static class TestContextConfiguration {

        @Bean
        @Autowired
        public UserMigrationService userMigrationService(ClientService clientService, MigrationService migrationService,
                                                         ClientDtoToUserDtoConverter clientDtoToUserDtoConverter,
                                                         RestTemplate restTemplate, ObjectMapper objectMapper,
                                                         ThreadPoolTaskExecutor threadPoolTaskExecutor) {
            return new UserMigrationServiceImpl(clientService, migrationService, clientDtoToUserDtoConverter,
                    restTemplate, objectMapper, threadPoolTaskExecutor);
        }
    }

    @Test
    void testUsersMigration() throws Exception {
        List<ClientDto> clientDtos = getObjectMapper().readValue(new ClassPathResource("client/client_dtos.json")
                .getInputStream(), new TypeReference<>() {
        });
        AuthResponseDto authResponseDto = getObjectFromJson("dto/auth_response_dto.json",
                AuthResponseDto.class);
        AvatarResponseDto avatarResponseDto = getObjectFromJson("dto/avatar_response_dto.json",
                AvatarResponseDto.class);
        List<UserDto> userDtos = getObjectMapper().readValue(new ClassPathResource("user/user_dtos.json")
                .getInputStream(), new TypeReference<>() {
        });

        when(threadPoolTaskExecutor.getActiveCount()).thenReturn(1);
        when(migrationService.saveSchedulerHistory()).thenReturn(1L);
        when(clientService.getClients(any(LocalDateTime.class))).thenReturn(clientDtos);
        when(migrationService.getMaxMigrationDate()).thenReturn(LocalDateTime.now());
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(AuthResponseDto.class)))
                .thenReturn(authResponseDto);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(AvatarResponseDto.class)))
                .thenReturn(avatarResponseDto);
        when(clientDtoToUserDtoConverter.convert(any(ClientDto.class))).thenReturn(userDtos.get(0), userDtos.get(1));
        doNothing().when(restTemplate).put(anyString(), any(HttpEntity.class), eq(String.class));
        doNothing().when(migrationService).saveMigration(any(ClientDto.class));

        userMigrationService.usersMigration();

        verify(threadPoolTaskExecutor).getActiveCount();
        verify(migrationService).saveSchedulerHistory();
        verify(clientService).getClients(any(LocalDateTime.class));
        verify(migrationService).getMaxMigrationDate();
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(AuthResponseDto.class));
        verify(restTemplate, times(2)).postForObject(anyString(), any(HttpEntity.class),
                eq(AvatarResponseDto.class));
        verify(clientDtoToUserDtoConverter, times(2)).convert(any(ClientDto.class));
        verify(restTemplate, times(2)).put(anyString(), any(HttpEntity.class), eq(String.class));
        verify(migrationService, times(2)).saveMigration(any(ClientDto.class));
    }

    @Test
    void testUserMigration() throws Exception {
        ClientDto clientDto = getObjectFromJson("client/client_dto.json", ClientDto.class);
        AuthResponseDto authResponseDto = getObjectFromJson("dto/auth_response_dto.json",
                AuthResponseDto.class);
        AvatarResponseDto avatarResponseDto = getObjectFromJson("dto/avatar_response_dto.json",
                AvatarResponseDto.class);
        UserDto userDto = getObjectFromJson("user/user_dto.json", UserDto.class);

        when(clientService.getClient(any(UUID.class))).thenReturn(clientDto);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(AuthResponseDto.class)))
                .thenReturn(authResponseDto);
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(AvatarResponseDto.class)))
                .thenReturn(avatarResponseDto);
        when(clientDtoToUserDtoConverter.convert(any(ClientDto.class))).thenReturn(userDto);
        doNothing().when(restTemplate).put(anyString(), any(HttpEntity.class), eq(String.class));
        doNothing().when(migrationService).saveMigration(any(ClientDto.class));

        userMigrationService.userMigration(UUID.randomUUID());

        verify(clientService).getClient(any(UUID.class));
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(AuthResponseDto.class));
        verify(restTemplate).postForObject(anyString(), any(HttpEntity.class), eq(AvatarResponseDto.class));
        verify(clientDtoToUserDtoConverter).convert(any(ClientDto.class));
        verify(restTemplate).put(anyString(), any(HttpEntity.class), eq(String.class));
        verify(migrationService).saveMigration(any(ClientDto.class));
    }

}
