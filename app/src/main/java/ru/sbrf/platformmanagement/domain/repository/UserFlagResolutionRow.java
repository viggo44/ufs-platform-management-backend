package ru.sbrf.platformmanagement.domain.repository;

/** Проекция для {@link FlagResolutionRepository#resolveForUser}. */
public interface UserFlagResolutionRow {

    Long getId();

    String getName();

    String getDescription();

    String getType();

    Boolean getValue();
}
