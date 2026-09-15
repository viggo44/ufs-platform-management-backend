package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.FlagUserValueEntity;
import ru.sbrf.platformmanagement.domain.model.FlagUserValueId;

import java.util.Collection;
import java.util.List;

public interface FlagUserValueRepository extends JpaRepository<FlagUserValueEntity, FlagUserValueId> {

    List<FlagUserValueEntity> findAllById_FlagId(Long flagId);

    @Modifying
    @Query("delete from FlagUserValueEntity f where f.id.flagId = :flagId")
    void deleteAllByFlagId(@Param("flagId") Long flagId);

    @Modifying
    @Query("delete from FlagUserValueEntity f where f.id.flagId = :flagId and f.id.userId in :userIds")
    void deleteByFlagIdAndUserIds(@Param("flagId") Long flagId, @Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query("delete from FlagUserValueEntity f where f.id.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
