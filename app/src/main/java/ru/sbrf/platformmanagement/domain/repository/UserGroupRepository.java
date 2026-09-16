package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroupEntity, Long>, JpaSpecificationExecutor<UserGroupEntity> {

    boolean existsByName(String name);

    /** Один запрос (коррелированный подзапрос), постоянная стоимость независимо от числа групп. */
    @Query("""
            select g from UserGroupEntity g
            where g.id in (select m.id.groupId from UserGroupMemberEntity m where m.id.userId = :userId)
            order by g.id
            """)
    List<UserGroupEntity> findAllByMemberUserId(@Param("userId") String userId);
}
