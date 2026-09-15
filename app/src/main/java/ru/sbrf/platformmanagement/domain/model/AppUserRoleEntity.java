package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Какая роль назначена пользователю на момент последней синхронизации.
 * Только insert/delete, поле никогда не апдейтится — {@code @Immutable} убирает
 * лишний dirty-checking снапшот и защищает от случайного UPDATE через managed-сущность.
 */
@Entity
@Table(name = "app_user_role")
@Getter
@Setter
@NoArgsConstructor
@Immutable
public class AppUserRoleEntity {

    @EmbeddedId
    private AppUserRoleId id;

    public AppUserRoleEntity(Long userId, String roleCode) {
        this.id = new AppUserRoleId(userId, roleCode);
    }
}
