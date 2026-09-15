package ru.sbrf.platformmanagement.ufs.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlagInfoDto {
    private List<ValuedFlagDto<UserDto>> users;
    private List<ValuedFlagDto<GroupDto>> groups;
}
