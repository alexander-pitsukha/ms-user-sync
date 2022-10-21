package by.nestegg.user.migration.persistence;

import by.nestegg.user.migration.enums.StatusType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "scheduler_history")
public class SchedulerHistory extends AbstractDomain<Long> {

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "status_type")
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

}
