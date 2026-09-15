package ru.sbrf.platformmanagement.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Контракт интеграции с сервисом логина (не выставляется наружу на фронт — поэтому не в
 * модуле {@code api}, а здесь, рядом с {@code UserSyncController}). Полный снимок
 * пользователя ССД, который сервис логина шлёт нам HTTP-запросом сразу после успешного
 * логина: id, ФИО, роли, пермишены — без обратного вызова за данными.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSyncRequest {
    @NotBlank(message = "Параметр userId не должен быть пустым")
    private String userId;
    private String username;
    private String fullName;
    private String lastName;
    private String firstName;
    private String middleName;
    @NotBlank(message = "Параметр employeeNumber не должен быть пустым")
    private String employeeNumber;
    private String departmentNumber;
    private String issuer;
    private List<String> roles;
    private List<String> permissions;
}
