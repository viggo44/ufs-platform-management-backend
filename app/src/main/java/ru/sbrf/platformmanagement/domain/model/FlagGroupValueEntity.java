package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Строка связи {@code ValuedFlag<Group>}. Простой скалярный id (без {@code @ManyToOne}).
 */
@Entity
@Table(name = "flag_group_value")
@Getter
@Setter
@NoArgsConstructor
public class FlagGroupValueEntity {

    @EmbeddedId
    private FlagGroupValueId id;

    @Column(nullable = false)
    private boolean value;

    private Instant updatedAt;

    public FlagGroupValueEntity(Long flagId, Long groupId, boolean value) {
        this.id = new FlagGroupValueId(flagId, groupId);
        this.value = value;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
