package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sbrf.platformmanagement.domain.model.SudirRoleDictEntity;

public interface SudirRoleDictRepository extends JpaRepository<SudirRoleDictEntity, String> {

    /**
     * Атомарный bulk-upsert: конфликт по PK — no-op, а не исключение. Без этого два
     * конкурентных логина с одним и тем же ранее не встречавшимся кодом роли гонятся за
     * одной вставкой — проигравший падает на unique constraint violation и откатывает
     * всю свою транзакцию синхронизации.
     */
    @Modifying
    @Query(value = "insert into ssv_db.sudir_role_dict(code, name) select c, c from unnest(:codes) as c "
            + "on conflict (code) do nothing", nativeQuery = true)
    void upsertMissing(@Param("codes") String[] codes);
}
