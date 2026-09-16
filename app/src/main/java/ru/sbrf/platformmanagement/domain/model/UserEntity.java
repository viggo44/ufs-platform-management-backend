package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Пользователь Orkestrum/platform-management, на которого нацеливаются флаги — и он же кэш
 * ССД-логина: по решению это одна таблица.
 *
 * <p>{@code id} — суррогатный PK, чисто внутренний якорь для FK из других таблиц
 * ({@code user_group_member}/{@code flag_user_value}/{@code user_sudir_role}/
 * {@code user_permission}). Публичный API по-прежнему адресует пользователя по
 * {@code tabNum} (согласовано) — {@code id} наружу не отдаётся, сервисный слой
 * транслирует {@code tabNum -> id} перед тем, как идти в связанные таблицы.
 *
 * <p>Поля {@code login}..{@code lastSyncedAt} заполняются только логином через отдельный
 * сервис логина (см. {@code domain.service.UserSyncWriter}) и остаются {@code null}, пока
 * человек ни разу не логинился — в том числе для пользователей, заведённых вручную.
 */
@Entity
@Table(name = "user", schema = "ssv_db")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", schema = "ssv_db", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tabNum;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    /** Реальный SUDIR-логин ССД. Заполняется только логином, до первого входа — null. */
    private String login;

    private String username;

    private String fullName;

    private String departmentNumber;

    private String issuer;

    private String fingerprint;

    private Instant lastSyncedAt;

    /**
     * Без payload — чистая связка, поэтому настоящий {@code @ManyToMany}, не отдельная
     * entity. Используется ТОЛЬКО для read-side JOIN в {@code UserSpecifications.memberOfGroup}
     * ({@code root.join("groups")} — один SQL JOIN, не отдельный запрос). Запись
     * (add/removeGroupUsers) идёт нативным bulk SQL напрямую по {@code user_group_member},
     * а не через эту коллекцию — грузить сущность только чтобы дёрнуть {@code .add()}/
     * {@code .remove()} на Set было бы медленнее и рискованнее, чем bulk insert/delete.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_group_member", schema = "ssv_db",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<UserGroupEntity> groups = new HashSet<>();

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
