package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.FlagGroupValueEntity;

import java.util.Collection;
import java.util.List;

public interface FlagGroupValueRepository extends JpaRepository<FlagGroupValueEntity, Long> {

    List<FlagGroupValueEntity> findAllByFlag_Id(Long flagId);

    List<FlagGroupValueEntity> findAllByGroup_Id(Long groupId);

    /** Явный bulk JPQL — см. {@link FlagUserValueRepository#deleteAllByFlagId} за обоснованием. */
    @Modifying
    @Query("delete from FlagGroupValueEntity f where f.flag.id = :flagId")
    void deleteAllByFlagId(@Param("flagId") Long flagId);

    @Modifying
    @Query("delete from FlagGroupValueEntity f where f.flag.id = :flagId and f.group.id in :groupIds")
    void deleteByFlagIdAndGroupIds(@Param("flagId") Long flagId, @Param("groupIds") Collection<Long> groupIds);

    @Modifying
    @Query("delete from FlagGroupValueEntity f where f.group.id = :groupId")
    void deleteAllByGroupId(@Param("groupId") Long groupId);
}
