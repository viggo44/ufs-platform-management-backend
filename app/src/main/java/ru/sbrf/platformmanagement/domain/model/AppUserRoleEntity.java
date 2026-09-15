package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Какая роль назначена пользователю на момент последней синхронизации. */
@Entity
@Table(name = "app_user_role")
@Getter
@Setter
@NoArgsConstructor
public class AppUserRoleEntity {

    @EmbeddedId
    private AppUserRoleId id;

    public AppUserRoleEntity(Long userId, String roleCode) {
        this.id = new AppUserRoleId(userId, roleCode);
    }
}
