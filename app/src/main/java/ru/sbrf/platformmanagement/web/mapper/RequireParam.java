package ru.sbrf.platformmanagement.web.mapper;

import jakarta.ws.rs.BadRequestException;

/**
 * Явно проверяет обязательные query-параметры вместо того, чтобы полагаться на
 * автоматическое срабатывание Bean Validation для вручную сконструированных объектов
 * (например {@code UfsPageRequest}, который не проходит через {@code @Valid} на входе
 * контроллера, раз собирается внутри метода из {@code page}/{@code limit}).
 */
public final class RequireParam {

    private RequireParam() {
    }

    public static <T> T notNull(T value, String paramName) {
        if (value == null) {
            throw new BadRequestException(paramName + " is required");
        }
        return value;
    }
}
