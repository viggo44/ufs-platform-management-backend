package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.FlagGroupValueEntity;
import ru.sbrf.platformmanagement.domain.model.FlagGroupValueId;

import java.util.Collection;
import java.util.List;

public interface FlagGroupValueRepository extends JpaRepository<FlagGroupValueEntity, FlagGroupValueId> {

    List<FlagGroupValueEntity> findAllById_FlagId(Long flagId);

    @Modifying
    @Query("delete from FlagGroupValueEntity f where f.id.flagId = :flagId")
    void deleteAllByFlagId(@Param("flagId") Long flagId);

    @Modifying
    @Query("delete from FlagGroupValueEntity f where f.id.flagId = :flagId and f.id.groupId in :groupIds")
    void deleteByFlagIdAndGroupIds(@Param("flagId") Long flagId, @Param("groupIds") Collection<Long> groupIds);

    @Modifying
    @Query("delete from FlagGroupValueEntity f where f.id.groupId = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);

    List<FlagGroupValueEntity> findAllById_GroupId(Long groupId);
}
