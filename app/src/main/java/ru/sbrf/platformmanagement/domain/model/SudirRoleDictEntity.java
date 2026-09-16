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
@Table(name = "sudir_role_dict", schema = "ssv_db")
@Getter
@Setter
@NoArgsConstructor
public class SudirRoleDictEntity {

    @Id
    private String code;

    @Column
    private String name;

    public SudirRoleDictEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
