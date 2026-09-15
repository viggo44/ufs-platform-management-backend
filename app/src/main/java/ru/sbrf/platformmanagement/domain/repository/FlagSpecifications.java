package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.sbrf.platformmanagement.domain.model.FlagEntity;

import java.util.Map;

public final class FlagSpecifications {

    /** Поле сортировки API -> путь свойства JPA. */
    public static final Map<String, String> SORT_WHITELIST = Map.of(
            "id", "id",
            "name", "name"
    );

    private FlagSpecifications() {
    }

    public static Specification<FlagEntity> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String pattern = "%" + Specifications.escapeLike(name.toLowerCase()) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern, '\\');
    }
}
