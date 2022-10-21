package by.nestegg.user.migration.persistence;

import by.nestegg.user.migration.enums.MigrationType;
import by.nestegg.user.migration.enums.StatusType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "migration")
public class Migration extends AbstractDomain<Long> {

    @Column(name = "client_external_id")
    private UUID clientExternalId;

    @Column(name = "migration_type")
    @Enumerated(EnumType.STRING)
    private MigrationType migrationType;

    @Column(name = "status_type")
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

}
