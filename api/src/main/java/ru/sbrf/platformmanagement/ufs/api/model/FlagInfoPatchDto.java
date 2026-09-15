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
public class FlagInfoPatchDto {
    private List<ValuedFlagDto<Long>> userIds;
    private List<ValuedFlagDto<Long>> groupIds;
}
