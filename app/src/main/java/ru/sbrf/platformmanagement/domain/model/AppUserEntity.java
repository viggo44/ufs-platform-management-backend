package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Пользователь Orkestrum/platform-management, на которого нацеливаются флаги — и он же кэш
 * ССД-логина: по решению это одна таблица. Ключом служит табельный номер ({@code tabNum}).
 * Поля {@code userId}..{@code lastSyncedAt} заполняются только логином через отдельный
 * сервис логина (см. {@code domain.service.UserSyncWriter}) и остаются {@code null}, пока
 * человек ни разу не логинился — в том числе для пользователей, заведённых вручную.
 */
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
public class AppUserEntity {

    @Id
    private String tabNum;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    /** Реальный SUDIR-идентификатор ССД. Заполняется только логином, до первого входа — null. */
    private String userId;

    private String username;

    private String fullName;

    private String departmentNumber;

    private String issuer;

    private String fingerprint;

    private Instant lastSyncedAt;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
