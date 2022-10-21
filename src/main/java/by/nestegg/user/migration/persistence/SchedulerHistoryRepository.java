package by.nestegg.user.migration.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SchedulerHistoryRepository extends JpaRepository<SchedulerHistory, Long> {

    @Query("select max(s.startedAt) from SchedulerHistory s where s.statusType = 'SUCCESS'")
    Optional<LocalDateTime> findMaxMigrationDate();

}
