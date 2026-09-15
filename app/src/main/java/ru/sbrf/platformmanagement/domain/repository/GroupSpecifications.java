package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.sbrf.platformmanagement.domain.model.UserGroupEntity;

import java.util.Map;

public final class GroupSpecifications {

    public static final Map<String, String> SORT_WHITELIST = Map.of(
            "id", "id",
            "name", "name"
    );

    private GroupSpecifications() {
    }

    public static Specification<UserGroupEntity> nameContains(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String pattern = "%" + Specifications.escapeLike(name.toLowerCase()) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), pattern, '\\');
    }
}
