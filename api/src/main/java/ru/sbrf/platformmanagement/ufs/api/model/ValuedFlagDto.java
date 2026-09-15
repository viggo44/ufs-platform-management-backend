package ru.sbrf.platformmanagement.ufs.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** {@code ValuedFlag<Data>} из ТЗ — значение в паре с тем, к чему оно относится. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValuedFlagDto<T> {
    private T data;
    private Boolean value;
}
