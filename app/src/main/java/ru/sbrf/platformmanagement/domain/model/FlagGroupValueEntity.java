package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Строка связи {@code ValuedFlag<Group>}. Суррогатный {@code id} + {@code @ManyToOne} —
 * см. {@link FlagUserValueEntity} за полным обоснованием (payload {@code value} не даёт
 * сделать это чистым {@code @ManyToMany}) и правилом против N+1 (оба конца — LAZY явно,
 * навигация только до {@code .getId()}, сущности читать отдельным bulk-запросом).
 */
@Entity
@Table(name = "flag_group_value", schema = "ssv_db",
        uniqueConstraints = @UniqueConstraint(columnNames = {"flag_id", "group_id"}))
@Getter
@Setter
@NoArgsConstructor
public class FlagGroupValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flag_group_value_id_seq")
    @SequenceGenerator(name = "flag_group_value_id_seq", sequenceName = "flag_group_value_id_seq",
            schema = "ssv_db", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_id", nullable = false)
    private FlagEntity flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroupEntity group;

    @Column(nullable = false)
    private boolean value;

    private Instant updatedAt;

    /** {@code flag}/{@code group} — {@code getReferenceById(...)}-прокси, см. {@link FlagUserValueEntity}. */
    public FlagGroupValueEntity(FlagEntity flag, UserGroupEntity group, boolean value) {
        this.flag = flag;
        this.group = group;
        this.value = value;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
