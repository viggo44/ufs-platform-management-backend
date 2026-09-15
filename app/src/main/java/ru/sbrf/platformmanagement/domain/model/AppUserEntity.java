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

import java.time.OffsetDateTime;

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
    @Column(name = "tab_num")
    private Long tabNum;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    /** Реальный SUDIR-идентификатор ССД. Заполняется только логином, до первого входа — null. */
    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "department_number")
    private String departmentNumber;

    @Column(name = "issuer")
    private String issuer;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "last_synced_at")
    private OffsetDateTime lastSyncedAt;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
