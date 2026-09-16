package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.UserGroupMemberEntity;
import ru.sbrf.platformmanagement.domain.model.UserGroupMemberId;

import java.util.Collection;

public interface UserGroupMemberRepository extends JpaRepository<UserGroupMemberEntity, UserGroupMemberId> {

    @Modifying
    @Query("delete from UserGroupMemberEntity m where m.id.groupId = :groupId and m.id.userId in :userIds")
    void deleteByGroupIdAndUserIds(@Param("groupId") Long groupId, @Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query("delete from UserGroupMemberEntity m where m.id.groupId = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);

    @Modifying
    @Query("delete from UserGroupMemberEntity m where m.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
