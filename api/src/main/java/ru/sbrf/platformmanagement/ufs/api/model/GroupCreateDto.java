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
public class GroupCreateDto {
    @NotBlank(message = "Параметр name не должен быть пустым")
    private String name;
    private String desc;
}
