package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.PermissionDictEntity;

public interface PermissionDictRepository extends JpaRepository<PermissionDictEntity, String> {

    /**
     * Атомарный bulk-upsert: конфликт по PK — no-op, а не исключение. См.
     * {@link SudirRoleDictRepository#upsertMissing} — то же соображение для пермишенов.
     */
    @Modifying
    @Query(value = "insert into ssv_db.permission_dict(code, name) select c, c from unnest(:codes) as c "
            + "on conflict (code) do nothing", nativeQuery = true)
    void upsertMissing(@Param("codes") String[] codes);
}
