package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.domain.Specification;

/** Небольшие общие вспомогательные методы для построения фильтров {@link Specification} для сущностей. */
public final class Specifications {

    private Specifications() {
    }

    @SafeVarargs
    @SuppressWarnings("deprecation")
    public static <T> Specification<T> allOf(Specification<T>... specs) {
        Specification<T> result = Specification.where(null);
        for (Specification<T> spec : specs) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }

    /** Экранирует {@code %}/{@code _}, чтобы недоверенный ввод не мог протащить спецсимволы SQL LIKE. */
    public static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
