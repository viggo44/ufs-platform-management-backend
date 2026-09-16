package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.FlagEntity;

import java.util.List;

/**
 * Один native-запрос, вычисляющий значение каждого флага для заданного пользователя,
 * приоритет {@code user > group > default}. Конфликт групп разрешается через
 * {@code bool_or} — побеждает true (согласовано). Постоянная стоимость независимо от
 * числа флагов/групп/переопределений.
 */
public interface FlagResolutionRepository extends Repository<FlagEntity, Long> {

    @Query(value = """
            select f.id as id, f.name as name, f.description as description,
              case when not f.with_customization then 'default'
                   when fuv.value is not null then 'user'
                   when gv.group_value is not null then 'group'
                   else 'default' end as type,
              case when not f.with_customization then f.default_value
                   when fuv.value is not null then fuv.value
                   when gv.group_value is not null then gv.group_value
                   else f.default_value end as value
            from flag f
            left join flag_user_value fuv on fuv.flag_id = f.id and fuv.user_id = :userId
            left join (
              select fgv.flag_id as flag_id, bool_or(fgv.value) as group_value
              from flag_group_value fgv
              join user_group_member m on m.group_id = fgv.group_id and m.user_id = :userId
              group by fgv.flag_id
            ) gv on gv.flag_id = f.id
            where (:customOnly = false or fuv.value is not null or gv.group_value is not null)
            order by f.id
            """, nativeQuery = true)
    List<UserFlagResolutionRow> resolveForUser(@Param("userId") String userId, @Param("customOnly") boolean customOnly);
}
