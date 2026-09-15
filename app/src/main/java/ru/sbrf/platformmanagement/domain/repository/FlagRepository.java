package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.sbrf.platformmanagement.domain.model.FlagEntity;

public interface FlagRepository extends JpaRepository<FlagEntity, Long>, JpaSpecificationExecutor<FlagEntity> {

    boolean existsByName(String name);
}
