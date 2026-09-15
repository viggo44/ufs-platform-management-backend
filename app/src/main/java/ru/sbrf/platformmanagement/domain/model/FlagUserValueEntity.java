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
 * Строка связи {@code ValuedFlag<User>}. Простой скалярный id (без {@code @ManyToOne}).
 */
@Entity
@Table(name = "flag_user_value")
@Getter
@Setter
@NoArgsConstructor
public class FlagUserValueEntity {

    @EmbeddedId
    private FlagUserValueId id;

    @Column(nullable = false)
    private boolean value;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public FlagUserValueEntity(Long flagId, Long userId, boolean value) {
        this.id = new FlagUserValueId(flagId, userId);
        this.value = value;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
