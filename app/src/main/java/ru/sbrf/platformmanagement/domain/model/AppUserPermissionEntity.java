package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Какой пермишен назначен пользователю на момент последней синхронизации. */
@Entity
@Table(name = "app_user_permission")
@Getter
@Setter
@NoArgsConstructor
public class AppUserPermissionEntity {

    @EmbeddedId
    private AppUserPermissionId id;

    public AppUserPermissionEntity(Long userId, String permissionCode) {
        this.id = new AppUserPermissionId(userId, permissionCode);
    }
}
