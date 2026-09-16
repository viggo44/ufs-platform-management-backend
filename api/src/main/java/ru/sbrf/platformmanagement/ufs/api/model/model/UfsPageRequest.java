package ru.sbrf.platformmanagement.ufs.api.model.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UfsPageRequest {
    @Min(value = 1, message = "Параметр page должен быть больше либо равен 1")
    @NotNull(message = "Параметр page не должен равняться null")
    private Integer page;

    @Min(value = 0, message = "Параметр limit должен быть больше либо равен 0")
    @NotNull(message = "Параметр limit не должен равняться null")
    private Integer limit;
}
