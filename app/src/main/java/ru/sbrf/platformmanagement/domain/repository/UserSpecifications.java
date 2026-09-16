package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.domain.Specification;
import ru.sbrf.platformmanagement.domain.model.UserEntity;

import java.util.Map;

public final class UserSpecifications {

    public static final Map<String, String> SORT_WHITELIST = Map.of(
            "id", "tabNum",
            "tabNum", "tabNum",
            "lastName", "lastName",
            "firstName", "firstName"
    );

    private UserSpecifications() {
    }

    public static Specification<UserEntity> lastNameContains(String value) {
        return contains("lastName", value);
    }

    public static Specification<UserEntity> firstNameContains(String value) {
        return contains("firstName", value);
    }

    public static Specification<UserEntity> tabNumEquals(String value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("tabNum"), value);
    }

    /**
     * JOIN через смаппленную {@code @ManyToMany}-связь ({@code UserEntity.groups}) — один
     * SQL JOIN в основном запросе, не отдельный запрос и не подзапрос. {@code distinct}
     * на случай общего использования этого Specification с другими join'ами — сама по себе
     * пара (user, group) уникальна, дублей от этого конкретного join не будет.
     */
    public static Specification<UserEntity> memberOfGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.equal(root.join("groups").get("id"), groupId);
        };
    }

    private static Specification<UserEntity> contains(String property, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String pattern = "%" + Specifications.escapeLike(value.toLowerCase()) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get(property)), pattern, '\\');
    }
}
