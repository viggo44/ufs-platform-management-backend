package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Какой пермишен назначен пользователю на момент последней синхронизации.
 * Только insert/delete, поле никогда не апдейтится — {@code @Immutable} убирает
 * лишний dirty-checking снапшот и защищает от случайного UPDATE через managed-сущность.
 */
@Entity
@Table(name = "user_permission", schema = "ssv_db")
@Getter
@Setter
@NoArgsConstructor
@Immutable
public class UserPermissionEntity {

    @EmbeddedId
    private UserPermissionId id;

    public UserPermissionEntity(Long userId, String permissionCode) {
        this.id = new UserPermissionId(userId, permissionCode);
    }
}
