package by.nestegg.user.migration.converter;

import by.nestegg.user.migration.Constants;
import by.nestegg.user.migration.client.Client;
import by.nestegg.user.migration.dto.ClientDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClientToDtoConverter implements Converter<Client, ClientDto> {

    @Override
    public ClientDto convert(Client source) {
        var clientDto = new ClientDto();
        clientDto.setId(source.getId());
        clientDto.setNickname(source.getNickname());
        clientDto.setFirstName(source.getFirstName());
        clientDto.setLastName(source.getLastName());
        clientDto.setEmail(source.getEmail());
        clientDto.setPassword(Optional.ofNullable(source.getPassword()).orElse(Constants.EMPTY));
        clientDto.setProfilePictureUuid(source.getProfilePictureUuid());
        clientDto.setExternalId(source.getExternalId());
        clientDto.setCreatedAt(source.getCreatedAt());
        clientDto.setUpdatedAt(source.getUpdatedAt());
        return clientDto;
    }

}
