package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.AppUserPermissionEntity;
import ru.sbrf.platformmanagement.domain.model.AppUserPermissionId;

import java.util.Collection;
import java.util.List;

public interface AppUserPermissionRepository extends JpaRepository<AppUserPermissionEntity, AppUserPermissionId> {

    List<AppUserPermissionEntity> findAllById_UserId(String userId);

    @Modifying
    @Query("delete from AppUserPermissionEntity p where p.id.userId = :userId and p.id.permissionCode not in :codes")
    void deleteStale(@Param("userId") String userId, @Param("codes") Collection<String> codes);

    @Modifying
    @Query("delete from AppUserPermissionEntity p where p.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);
}
