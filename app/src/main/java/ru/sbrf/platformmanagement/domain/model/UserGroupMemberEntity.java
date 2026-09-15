package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Строка связи членства в группе. Простые скалярные id (без {@code @ManyToOne}) — чтение
 * идёт через явные джойны в репозитории, а не через навигацию по графу объектов.
 * Только insert/delete, поле никогда не апдейтится — {@code @Immutable} убирает
 * лишний dirty-checking снапшот и защищает от случайного UPDATE через managed-сущность.
 */
@Entity
@Table(name = "user_group_member")
@Getter
@Setter
@NoArgsConstructor
@Immutable
public class UserGroupMemberEntity {

    @EmbeddedId
    private UserGroupMemberId id;

    public UserGroupMemberEntity(Long userId, Long groupId) {
        this.id = new UserGroupMemberId(userId, groupId);
    }
}
