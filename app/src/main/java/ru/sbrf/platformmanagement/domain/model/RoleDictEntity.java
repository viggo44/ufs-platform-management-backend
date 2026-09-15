package ru.sbrf.platformmanagement.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Справочник кодов ролей ССД, встреченных через логин. Заполняется только login-sync. */
@Entity
@Table(name = "role_dict")
@Getter
@Setter
@NoArgsConstructor
public class RoleDictEntity {

    @Id
    private String code;

    @Column
    private String name;

    public RoleDictEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
