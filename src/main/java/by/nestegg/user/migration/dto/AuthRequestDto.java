package by.nestegg.user.migration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDto {

    private String type;

    private String user;

    private String password;

}
