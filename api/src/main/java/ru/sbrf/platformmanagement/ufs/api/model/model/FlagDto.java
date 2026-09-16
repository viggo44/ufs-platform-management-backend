package ru.sbrf.platformmanagement.ufs.api.model.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlagDto {
    private Long id;
    private String name;
    private String desc;
    private Boolean defaultValue;
    private Boolean withCustomization;
}
