package by.nestegg.user.migration.service;

import by.nestegg.user.migration.AbstractTests;
import by.nestegg.user.migration.Constants;
import by.nestegg.user.migration.UserMigrationApplication;
import by.nestegg.user.migration.config.ClientConfiguration;
import by.nestegg.user.migration.config.MigrationConfiguration;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.persistence.MigrationRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserMigrationApplication.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureWireMock(port = 9999)
@SqlGroup({
        @Sql(scripts = "classpath:sql/hsqld/client_schema.sql",
                config = @SqlConfig(dataSource = ClientConfiguration.CLIENT_DATA_SOURCE,
                        transactionManager = ClientConfiguration.CLIENT_TRANSACTION_MANAGER)),
        @Sql(scripts = "classpath:sql/client_drop.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
                config = @SqlConfig(dataSource = ClientConfiguration.CLIENT_DATA_SOURCE,
                        transactionManager = ClientConfiguration.CLIENT_TRANSACTION_MANAGER)),
        @Sql(scripts = "classpath:sql/h2/migration_schema.sql",
                config = @SqlConfig(dataSource = MigrationConfiguration.MIGRATION_DATA_SOURCE,
                        transactionManager = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER)),
        @Sql(scripts = "classpath:sql/h2/migration_drop.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
                config = @SqlConfig(dataSource = MigrationConfiguration.MIGRATION_DATA_SOURCE,
                        transactionManager = MigrationConfiguration.MIGRATION_TRANSACTION_MANAGER))})
class UserMigrationServiceImplIntegrationTests extends AbstractTests {
    @Autowired
    private UserMigrationService userMigrationService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private MigrationRepository migrationRepository;
    @Value("${chat.user.domain}")
    private String chatUserDomain;

    @Test
    @Sql(scripts = "classpath:sql/import_client.sql",
            config = @SqlConfig(dataSource = ClientConfiguration.CLIENT_DATA_SOURCE,
                    transactionManager = ClientConfiguration.CLIENT_TRANSACTION_MANAGER))
    @SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
    void testUserMigration() throws Exception {
        ClientDto clientDto = clientService.getClient(UUID.fromString("613fbdb4-e2d2-4a02-a298-472a24b52ee2"));
        stubResponses(clientDto);

        userMigrationService.userMigration(clientDto.getExternalId());
        boolean result = migrationRepository.existsByClientExternalId(clientDto.getExternalId());

        assertTrue(result);
    }

    private void stubResponses(ClientDto clientDto) throws IOException {
        var uri = UriComponentsBuilder.fromUriString("")
                .pathSegment("_matrix/client/r0/login")
                .build().toUriString();
        stubResponse(uri, "dto/auth_response_dto.json");
        uri = UriComponentsBuilder.fromUriString("")
                .pathSegment("admin/clients/profile-picture")
                .queryParam(Constants.UUID, clientDto.getProfilePictureUuid())
                .build().toUriString();
        stubByteResponse(uri);
        uri = UriComponentsBuilder.fromUriString("")
                .pathSegment("_matrix/media/r0/upload")
                .queryParam(Constants.FILENAME, clientDto.getProfilePictureUuid() + Constants.PNG)
                .build().toUriString();
        stubResponse(uri, "dto/avatar_response_dto.json");
        String userId = getUserId(clientDto);
        uri = UriComponentsBuilder.fromUriString("")
                .pathSegment("_synapse/admin/v2/users").pathSegment(userId)
                .build().toUriString();
        stubCreateOrUpdateUser(uri);
    }

    private void stubResponse(String uri, String responseRecourse) throws IOException {
        String response = FileUtils.readFileToString(new ClassPathResource(responseRecourse).getFile(),
                StandardCharsets.UTF_8);
        stubFor(post(urlEqualTo(uri))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(response)));
    }

    private void stubByteResponse(String uri) {
        stubFor(get(urlEqualTo(uri))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new byte[]{})));
    }

    private void stubCreateOrUpdateUser(String uri) {
        stubFor(put(urlEqualTo(uri))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(HttpStatus.NO_CONTENT.value())));
    }

    private String getUserId(ClientDto clientDto) {
        return Constants.AT + clientDto.getExternalId() + Constants.COLON + chatUserDomain;
    }

}
