package ru.sbrf.platformmanagement.ufs.api.model.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPatchDto {
    private String name;
    private String desc;
}
