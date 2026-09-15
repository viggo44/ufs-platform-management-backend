package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sbrf.platformmanagement.domain.model.RoleDictEntity;

public interface RoleDictRepository extends JpaRepository<RoleDictEntity, String> {
}
