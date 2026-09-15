package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Строка связи членства в группе. Простые скалярные id (без {@code @ManyToOne}) — чтение
 * идёт через явные джойны в репозитории, а не через навигацию по графу объектов.
 */
@Entity
@Table(name = "user_group_member")
@Getter
@Setter
@NoArgsConstructor
public class UserGroupMemberEntity {

    @EmbeddedId
    private UserGroupMemberId id;

    public UserGroupMemberEntity(Long userId, Long groupId) {
        this.id = new UserGroupMemberId(userId, groupId);
    }
}
