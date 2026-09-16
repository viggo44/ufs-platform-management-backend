package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.UserSudirRoleEntity;
import ru.sbrf.platformmanagement.domain.model.UserSudirRoleId;

import java.util.Collection;
import java.util.List;

public interface UserSudirRoleRepository extends JpaRepository<UserSudirRoleEntity, UserSudirRoleId> {

    List<UserSudirRoleEntity> findAllById_UserId(Long userId);

    @Modifying
    @Query("delete from UserSudirRoleEntity r where r.id.userId = :userId and r.id.sudirRoleCode not in :codes")
    void deleteStale(@Param("userId") Long userId, @Param("codes") Collection<String> codes);

    @Modifying
    @Query("delete from UserSudirRoleEntity r where r.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
