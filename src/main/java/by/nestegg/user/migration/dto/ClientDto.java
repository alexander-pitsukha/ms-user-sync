package by.nestegg.user.migration.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ClientDto {

    private Long id;

    private String nickname;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private UUID profilePictureUuid;

    private UUID externalId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
