package by.nestegg.user.migration.service.impl;

import by.nestegg.user.migration.Constants;
import by.nestegg.user.migration.converter.ClientDtoToUserDtoConverter;
import by.nestegg.user.migration.dto.AuthRequestDto;
import by.nestegg.user.migration.dto.AuthResponseDto;
import by.nestegg.user.migration.dto.AvatarResponseDto;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.dto.UserDto;
import by.nestegg.user.migration.enums.StatusType;
import by.nestegg.user.migration.exception.ServiceException;
import by.nestegg.user.migration.service.ClientService;
import by.nestegg.user.migration.service.MigrationService;
import by.nestegg.user.migration.service.UserMigrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMigrationServiceImpl implements UserMigrationService {

    private final ClientService clientService;
    private final MigrationService migrationService;
    private final ClientDtoToUserDtoConverter clientDtoToUserDtoConverter;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Value("${chat.base.url}")
    private String chatBaseUrl;
    @Value("${chat.user.domain}")
    private String chatUserDomain;
    @Value("${chat.type}")
    private String type;
    @Value("${chat.user}")
    private String user;
    @Value("${chat.password}")
    private String password;
    @Value("${client.server.url}")
    private String clientServerUrl;

    @Override
    @Async
    @Transactional
    public void usersMigration() {
        if (threadPoolTaskExecutor.getActiveCount() > 1) {
            return;
        }
        log.info("Start users migration.");
        var schedulerHistoryId = migrationService.saveSchedulerHistory();
        var statusType = StatusType.SUCCESS;
        try {
            List<ClientDto> clientDtos = clientService.getClients(migrationService.getMaxMigrationDate());
            if (!clientDtos.isEmpty()) {
                var authResponseDto = getAuthResponse();
                clientDtos.forEach(clientDto -> userMigrate(authResponseDto, clientDto));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            statusType = StatusType.FAIL;
        }
        migrationService.updateSchedulerHistory(schedulerHistoryId, statusType);
        log.info("Finish users migration.");
    }

    @Override
    public void userMigration(final UUID externalId) {
        log.info("Start user migration - externalId = {}.", externalId);
        var clientDto = clientService.getClient(externalId);
        var authResponseDto = getAuthResponse();
        if (clientDto != null && authResponseDto != null) {
            userMigrate(authResponseDto, clientDto);
        }
        log.info("Finish user migration.");
    }

    private AuthResponseDto getAuthResponse() {
        log.info("Get auth response.");
        var authRequestDto = new AuthRequestDto();
        authRequestDto.setType(type);
        authRequestDto.setUser(user);
        authRequestDto.setPassword(password);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var uri = UriComponentsBuilder.fromUriString(chatBaseUrl)
                .pathSegment("_matrix/client/r0/login")
                .build().toUriString();
        HttpEntity<AuthRequestDto> httpEntity = new HttpEntity<>(authRequestDto, headers);
        return restTemplate.postForObject(uri, httpEntity, AuthResponseDto.class);
    }

    private void userMigrate(AuthResponseDto authResponseDto, ClientDto clientDto) {
        Optional.ofNullable(clientDtoToUserDtoConverter.convert(clientDto)).ifPresent(userDto -> {
            if (clientDto.getProfilePictureUuid() != null) {
                var avatarResponseDto = uploadAvatar(authResponseDto,
                        clientDto.getProfilePictureUuid());
                userDto.setAvatarUrl(avatarResponseDto.getContentUri());
            } else {
                userDto.setAvatarUrl(Constants.EMPTY);
            }
            createOrUpdateUser(authResponseDto, userDto, getUserId(clientDto));
        });
        migrationService.saveMigration(clientDto);
    }

    private AvatarResponseDto uploadAvatar(AuthResponseDto authResponseDto, UUID profilePictureUuid) {
        log.info("Upload avatar.");
        var uri = UriComponentsBuilder.fromUriString(clientServerUrl)
                .pathSegment("admin/clients/profile-picture")
                .queryParam(Constants.UUID, profilePictureUuid)
                .build().toUriString();
        byte[] imageBytes = restTemplate.getForObject(uri, byte[].class);
        uri = UriComponentsBuilder.fromUriString(chatBaseUrl)
                .pathSegment("_matrix/media/r0/upload")
                .queryParam(Constants.FILENAME, profilePictureUuid + Constants.PNG)
                .build().toUriString();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(authResponseDto.getAccessToken());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(Constants.FILE, imageBytes);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(uri, httpEntity, AvatarResponseDto.class);
    }

    private void createOrUpdateUser(AuthResponseDto authResponseDto, UserDto userDto, String userId) {
        log.info("Create or update user. userId = {}.", userId);
        try {
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authResponseDto.getAccessToken());
            var uri = UriComponentsBuilder.fromUriString(chatBaseUrl)
                    .pathSegment("_synapse/admin/v2/users").pathSegment(userId)
                    .build().toUriString();
            HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(userDto), headers);
            restTemplate.put(uri, httpEntity, String.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    private String getUserId(ClientDto clientDto) {
        return Constants.AT + clientDto.getExternalId() + Constants.COLON + chatUserDomain;
    }

}
