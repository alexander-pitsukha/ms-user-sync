package by.nestegg.user.migration.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("select c from Client c where (c.createdAt > :lastDateTime and c.updatedAt is null) or (c.updatedAt > :lastDateTime)")
    Stream<Client> findAllByCreatedAtAndUpdateAt(@Param("lastDateTime") LocalDateTime lastDateTime);

    @Query("select c from Client c where c.externalId = :externalId")
    Client findByExternalId(@Param("externalId") UUID externalId);

}
