package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Какая роль ССД назначена пользователю на момент последней синхронизации.
 * Только insert/delete, поле никогда не апдейтится — {@code @Immutable} убирает
 * лишний dirty-checking снапшот и защищает от случайного UPDATE через managed-сущность.
 */
@Entity
@Table(name = "user_sudir_role", schema = "ssv_db")
@Getter
@Setter
@NoArgsConstructor
@Immutable
public class UserSudirRoleEntity {

    @EmbeddedId
    private UserSudirRoleId id;

    public UserSudirRoleEntity(Long userId, String sudirRoleCode) {
        this.id = new UserSudirRoleId(userId, sudirRoleCode);
    }
}
