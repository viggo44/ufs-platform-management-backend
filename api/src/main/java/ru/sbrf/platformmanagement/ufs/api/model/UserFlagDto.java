package ru.sbrf.platformmanagement.ufs.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFlagDto {
    private Long id;
    private String name;
    private String desc;
    private FlagType type;
    private Boolean value;
}
