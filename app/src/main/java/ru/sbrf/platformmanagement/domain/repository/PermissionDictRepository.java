package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.sbrf.platformmanagement.domain.model.PermissionDictEntity;

public interface PermissionDictRepository extends JpaRepository<PermissionDictEntity, String> {
}
