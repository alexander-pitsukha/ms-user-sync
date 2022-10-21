package by.nestegg.user.migration.repository;

import by.nestegg.user.migration.client.Client;
import by.nestegg.user.migration.client.ClientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@SqlGroup({
        @Sql(scripts = "classpath:sql/h2/client_schema.sql"),
        @Sql(scripts = "classpath:sql/client_drop.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)})
class ClientRepositoryTests extends AbstractRepositoryTests<Client> {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testFindAllByCreatedAtAndUpdateAt() throws Exception {
        List<Client> clients = getObjectMapper().readValue(new ClassPathResource("client/clients.json")
                .getInputStream(), new TypeReference<>() {
        });
        clients.forEach(this::saveTestEntity);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Stream<Client> entitiesStream = clientRepository.findAllByCreatedAtAndUpdateAt(
                LocalDateTime.parse("2021-04-01 00:00:00", formatter));
        List<Client> entities = entitiesStream.collect(Collectors.toList());

        assertNotNull(entities);
        assertEquals(clients.size(), entities.size());
    }

    @Test
    void testFindByExternalId() throws Exception {
        Client client = saveTestEntity("client/client.json", Client.class);

        Client entity = clientRepository.findByExternalId(client.getExternalId());

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(client.getNickname(), entity.getNickname());
        assertEquals(client.getFirstName(), entity.getFirstName());
        assertEquals(client.getLastName(), entity.getLastName());
        assertEquals(client.getEmail(), entity.getEmail());
        assertEquals(client.getPassword(), entity.getPassword());
        assertEquals(client.getProfilePictureUuid(), entity.getProfilePictureUuid());
        assertEquals(client.getExternalId(), entity.getExternalId());
        assertEquals(client.getCreatedAt(), entity.getCreatedAt());
        assertEquals(client.getUpdatedAt(), entity.getUpdatedAt());
    }

}
