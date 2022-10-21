package by.nestegg.user.migration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserDto {

    @JsonProperty("displayname")
    private String displayName;

    private String password;

    @JsonProperty("threepids")
    private List<ThreePidsDto> threePidsDtos = new ArrayList<>();

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("admin")
    private Boolean isAdmin;

    private Boolean deactivated;

}
