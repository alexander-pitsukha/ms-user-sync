package by.nestegg.user.migration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvatarResponseDto {

    @JsonProperty("content_uri")
    private String contentUri;

}
