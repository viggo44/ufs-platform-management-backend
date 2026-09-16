package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.AppUserRoleEntity;
import ru.sbrf.platformmanagement.domain.model.AppUserRoleId;

import java.util.Collection;
import java.util.List;

public interface AppUserRoleRepository extends JpaRepository<AppUserRoleEntity, AppUserRoleId> {

    List<AppUserRoleEntity> findAllById_UserId(String userId);

    @Modifying
    @Query("delete from AppUserRoleEntity r where r.id.userId = :userId and r.id.roleCode not in :codes")
    void deleteStale(@Param("userId") String userId, @Param("codes") Collection<String> codes);

    @Modifying
    @Query("delete from AppUserRoleEntity r where r.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") String userId);
}
