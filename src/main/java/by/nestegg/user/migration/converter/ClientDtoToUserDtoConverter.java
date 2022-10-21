package by.nestegg.user.migration.converter;

import by.nestegg.user.migration.Constants;
import by.nestegg.user.migration.dto.ClientDto;
import by.nestegg.user.migration.dto.ThreePidsDto;
import by.nestegg.user.migration.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Optional;

@Component
public class ClientDtoToUserDtoConverter implements Converter<ClientDto, UserDto> {

    @Override
    public UserDto convert(ClientDto source) {
        var userDto = new UserDto();
        userDto.setDisplayName(StringUtils.hasLength(source.getNickname()) ? source.getNickname()
                : source.getFirstName() + Constants.SPACE + source.getLastName());
        userDto.setPassword(source.getPassword());
        Optional.ofNullable(source.getEmail()).ifPresent(email -> {
            var threePidsDto = new ThreePidsDto();
            threePidsDto.setMedium(Constants.EMAIL);
            threePidsDto.setAddress(email);
            userDto.setThreePidsDtos(Collections.singletonList(threePidsDto));
        });
        userDto.setIsAdmin(Boolean.FALSE);
        userDto.setDeactivated(Boolean.FALSE);
        return userDto;
    }

}
