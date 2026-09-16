package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.FlagUserValueEntity;

import java.util.Collection;
import java.util.List;

public interface FlagUserValueRepository extends JpaRepository<FlagUserValueEntity, Long> {

    List<FlagUserValueEntity> findAllByFlag_Id(Long flagId);

    /**
     * Явный bulk JPQL, а не derived {@code deleteAllBy...} — тот сначала делает SELECT,
     * потом удаляет каждую строку по отдельности через {@code EntityManager.remove()}
     * (чтобы отработали lifecycle-колбэки), а это N+1 при каскадном удалении флага с
     * множеством override'ов. Явный {@code delete from ... where} — один statement.
     */
    @Modifying
    @Query("delete from FlagUserValueEntity f where f.flag.id = :flagId")
    void deleteAllByFlagId(@Param("flagId") Long flagId);

    @Modifying
    @Query("delete from FlagUserValueEntity f where f.flag.id = :flagId and f.user.id in :userIds")
    void deleteByFlagIdAndUserIds(@Param("flagId") Long flagId, @Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query("delete from FlagUserValueEntity f where f.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
