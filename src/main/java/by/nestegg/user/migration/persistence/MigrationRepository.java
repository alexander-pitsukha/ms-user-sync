package by.nestegg.user.migration.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MigrationRepository extends JpaRepository<Migration, Long> {

    @Query("select case when count(m) > 0 then true else false end from Migration m where exists (select '*' from Migration mi where mi.clientExternalId = :clientExternalId)")
    boolean existsByClientExternalId(@Param("clientExternalId") UUID clientExternalId);

}
