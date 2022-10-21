package by.nestegg.user.migration.service.impl;

import by.nestegg.user.migration.client.Client;
import by.nestegg.user.migration.client.ClientRepository;
import by.nestegg.user.migration.config.ClientConfiguration;
import by.nestegg.user.migration.converter.ClientToDtoConverter;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(value = ClientConfiguration.CLIENT_TRANSACTION_MANAGER, readOnly = true)
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientToDtoConverter clientToDtoConverter;

    @Override
    public List<ClientDto> getClients(final LocalDateTime lastDateTime) {
        log.info("Get clients.");
        Stream<Client> clients = lastDateTime != null ? clientRepository.findAllByCreatedAtAndUpdateAt(lastDateTime)
                : clientRepository.findAll().stream();
        return clients.map(clientToDtoConverter::convert).collect(Collectors.toList());
    }

    @Override
    public ClientDto getClient(final UUID externalId) {
        log.info("Get client by externalId = {}.", externalId);
        return clientToDtoConverter.convert(clientRepository.findByExternalId(externalId));
    }

}
