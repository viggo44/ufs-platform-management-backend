package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.sbrf.platformmanagement.domain.model.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByTabNum(String tabNum);

    boolean existsByTabNum(String tabNum);

    /** Массовый резолв tab_num -> сущность (с внутренним id) для body-параметров вида userIds. */
    List<UserEntity> findAllByTabNumIn(Collection<String> tabNums);
}
