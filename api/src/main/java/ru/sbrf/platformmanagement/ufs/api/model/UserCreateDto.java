package ru.sbrf.platformmanagement.ufs.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotNull(message = "Параметр tabNum не должен равняться null")
    private Long tabNum;
    @NotBlank(message = "Параметр lastName не должен быть пустым")
    private String lastName;
    @NotBlank(message = "Параметр firstName не должен быть пустым")
    private String firstName;
    private String middleName;
}
