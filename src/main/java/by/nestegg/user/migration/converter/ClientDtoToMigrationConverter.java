package by.nestegg.user.migration.converter;

import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.enums.StatusType;
import by.nestegg.user.migration.persistence.Migration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoToMigrationConverter implements Converter<ClientDto, Migration> {

    @Override
    public Migration convert(ClientDto source) {
        var migration = new Migration();
        migration.setClientExternalId(source.getExternalId());
        migration.setStatusType(StatusType.SUCCESS);
        return migration;
    }

}
