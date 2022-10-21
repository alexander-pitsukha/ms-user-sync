package by.nestegg.user.migration.service;

import by.nestegg.user.migration.dto.ClientDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClientService {

    List<ClientDto> getClients(LocalDateTime lastDateTime);

    ClientDto getClient(UUID externalId);

}
