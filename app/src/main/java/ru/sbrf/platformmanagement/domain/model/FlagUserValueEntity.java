package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Строка связи {@code ValuedFlag<User>}. Суррогатный {@code id} + {@code @ManyToOne} вместо
 * составного ключа — у таблицы есть payload ({@code value}), поэтому чистый
 * {@code @ManyToMany} невозможен (JPA не поддерживает лишние колонки в join-таблице).
 *
 * <p><b>Важно для избежания N+1:</b> оба {@code @ManyToOne} — {@code LAZY} явно (дефолт
 * JPA для {@code @ManyToOne} — {@code EAGER}, но EAGER не значит JOIN: Hibernate может
 * выполнить отдельный SELECT на каждую строку). Код никогда не должен вызывать
 * {@code getFlag()}/{@code getUser()} для доступа к чему-либо, кроме id, в цикле по списку
 * таких строк — только {@code .getUser().getId()} (не бьёт в БД, id уже в FK-колонке), а
 * сами сущности читать отдельным bulk {@code findAllById}.
 */
@Entity
@Table(name = "flag_user_value", schema = "ssv_db",
        uniqueConstraints = @UniqueConstraint(columnNames = {"flag_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
public class FlagUserValueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flag_user_value_id_seq")
    @SequenceGenerator(name = "flag_user_value_id_seq", sequenceName = "flag_user_value_id_seq",
            schema = "ssv_db", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_id", nullable = false)
    private FlagEntity flag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private boolean value;

    private Instant updatedAt;

    /**
     * {@code flag}/{@code user} ожидаются как {@code getReferenceById(...)}-прокси со
     * стороны вызывающего кода (см. {@code FlagService}) — без лишнего {@code SELECT}
     * только чтобы проставить FK на insert.
     */
    public FlagUserValueEntity(FlagEntity flag, UserEntity user, boolean value) {
        this.flag = flag;
        this.user = user;
        this.value = value;
    }

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
