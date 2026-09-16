package ru.sbrf.platformmanagement.ufs.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@code id} и {@code tabNum} здесь всегда равны — идентификатором служит табельный номер
 * (согласованное решение для отсутствующего в ТЗ поля {@code userId} у создания). Сам
 * {@code userId} — настоящий SUDIR-идентификатор ССД, заполняется только после первого
 * логина человека и до тех пор равен {@code null}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String tabNum;
    private String lastName;
    private String firstName;
    private String middleName;
    private String userId;
}
