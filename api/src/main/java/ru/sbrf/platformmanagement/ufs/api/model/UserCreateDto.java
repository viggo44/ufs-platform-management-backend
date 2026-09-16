package ru.sbrf.platformmanagement.ufs.api.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Параметр tabNum не должен быть пустым")
    private String tabNum;
    @NotBlank(message = "Параметр lastName не должен быть пустым")
    private String lastName;
    @NotBlank(message = "Параметр firstName не должен быть пустым")
    private String firstName;
    private String middleName;
}
