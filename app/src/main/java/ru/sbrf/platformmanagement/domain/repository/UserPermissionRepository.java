package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.UserPermissionEntity;
import ru.sbrf.platformmanagement.domain.model.UserPermissionId;

import java.util.Collection;
import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermissionEntity, UserPermissionId> {

    List<UserPermissionEntity> findAllById_UserId(Long userId);

    @Modifying
    @Query("delete from UserPermissionEntity p where p.id.userId = :userId and p.id.permissionCode not in :codes")
    void deleteStale(@Param("userId") Long userId, @Param("codes") Collection<String> codes);

    @Modifying
    @Query("delete from UserPermissionEntity p where p.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
