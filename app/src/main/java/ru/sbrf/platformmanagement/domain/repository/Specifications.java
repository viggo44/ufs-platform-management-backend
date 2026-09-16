package ru.sbrf.platformmanagement.domain.repository;

import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Objects;

/** Небольшие общие вспомогательные методы для построения фильтров {@link Specification} для сущностей. */
public final class Specifications {

    private Specifications() {
    }

    /**
     * {@code Specification.where(null)} deprecated и помечен к удалению — {@code allOf}
     * (добавлен в этой же версии как замена) сам корректно собирает пустой/единственный
     * список в "матчит всё", без ручного null-стартера.
     */
    @SafeVarargs
    public static <T> Specification<T> allOf(Specification<T>... specs) {
        return Specification.allOf(Arrays.stream(specs).filter(Objects::nonNull).toList());
    }

    /** Экранирует {@code %}/{@code _}, чтобы недоверенный ввод не мог протащить спецсимволы SQL LIKE. */
    public static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
