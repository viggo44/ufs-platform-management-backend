package ru.sbrf.platformmanagement.domain.repository;

import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import ru.sbrf.platformmanagement.domain.model.AppUserEntity;
import ru.sbrf.platformmanagement.domain.model.UserGroupMemberEntity;

import java.util.Map;

public final class AppUserSpecifications {

    public static final Map<String, String> SORT_WHITELIST = Map.of(
            "id", "tabNum",
            "tabNum", "tabNum",
            "lastName", "lastName",
            "firstName", "firstName"
    );

    private AppUserSpecifications() {
    }

    public static Specification<AppUserEntity> lastNameContains(String value) {
        return contains("lastName", value);
    }

    public static Specification<AppUserEntity> firstNameContains(String value) {
        return contains("firstName", value);
    }

    public static Specification<AppUserEntity> tabNumEquals(Long value) {
        if (value == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("tabNum"), value);
    }

    /** Коррелированный подзапрос к таблице связей членства — один join, без N+1. */
    public static Specification<AppUserEntity> memberOfGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            var member = subquery.from(UserGroupMemberEntity.class);
            subquery.select(member.get("id").get("userId"));
            subquery.where(cb.equal(member.get("id").get("groupId"), groupId));
            return root.get("tabNum").in(subquery);
        };
    }

    private static Specification<AppUserEntity> contains(String property, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String pattern = "%" + Specifications.escapeLike(value.toLowerCase()) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get(property)), pattern, '\\');
    }
}
