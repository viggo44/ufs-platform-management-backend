package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;

import java.util.Collection;
import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Long>, JpaSpecificationExecutor<UserGroupEntity> {

    boolean existsByName(String name);

    /**
     * JOIN через смаппленную {@code @ManyToMany}-связь ({@code UserEntity.groups}) —
     * один SQL JOIN, не отдельный запрос на коллекцию. Постоянная стоимость независимо
     * от числа групп.
     */
    @Query("select g from UserEntity u join u.groups g where u.id = :userId order by g.id")
    List<UserGroupEntity> findAllByMemberUserId(@Param("userId") Long userId);

    /**
     * Членство в группе — чистая связка без payload, на Java-стороне представлена
     * {@code @ManyToMany} (только для read-side JOIN), а не отдельной entity — запись идёт
     * нативным SQL напрямую по {@code user_group_member}, не через управление коллекцией
     * (грузить сущности только чтобы дёрнуть {@code .add()}/{@code .remove()} на Set было
     * бы медленнее и рискованнее bulk insert/delete). {@code ON CONFLICT DO NOTHING} —
     * повторное добавление уже существующего участника молча не делает ничего, а не падает.
     */
    @Modifying
    @Query(value = "insert into ssv_db.user_group_member(user_id, group_id) "
            + "select u, :groupId from unnest(:userIds) as u on conflict do nothing", nativeQuery = true)
    void addMembers(@Param("groupId") Long groupId, @Param("userIds") Long[] userIds);

    @Modifying
    @Query(value = "delete from ssv_db.user_group_member where group_id = :groupId and user_id in (:userIds)",
            nativeQuery = true)
    void removeMembers(@Param("groupId") Long groupId, @Param("userIds") Collection<Long> userIds);

    @Modifying
    @Query(value = "delete from ssv_db.user_group_member where group_id = :groupId", nativeQuery = true)
    void deleteAllByGroupId(@Param("groupId") Long groupId);

    @Modifying
    @Query(value = "delete from ssv_db.user_group_member where user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Long userId);
}
