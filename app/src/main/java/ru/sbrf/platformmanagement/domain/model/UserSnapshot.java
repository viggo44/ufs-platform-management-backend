package ru.sbrf.platformmanagement.domain.model;

import java.util.List;

/**
 * Не JPA-сущность — неизменяемый снимок данных о пользователе от сервиса логина, лежит
 * здесь как модель данных (по инструкции "все entity будут лежать в model" трактуется
 * широко — это тоже модель данных домена, просто не персистентная напрямую).
 *
 * @param roleCodes       отсортированы, без дублей
 * @param permissionCodes отсортированы, без дублей
 * @param fingerprint     hex-дайджест SHA-256 по всем полям выше; используется, чтобы
 *                        пропустить пересинхронизацию, если ничего не изменилось
 * @param tabNum          {@code employeeNumberRaw}, обрезанный по краям, или {@code null},
 *                        если пусто — табельный номер может быть не чисто числовым, поэтому
 *                        это строка, а не распарсенное число
 * @param login           реальный SUDIR-логин ССД (в запросе от сервиса логина называется
 *                        {@code userId} — внешний контракт, здесь переименован в {@code login}
 *                        под нашу терминологию)
 */
public record UserSnapshot(
        String login,
        String username,
        String fullName,
        String lastName,
        String firstName,
        String middleName,
        String employeeNumberRaw,
        String tabNum,
        String departmentNumber,
        String issuer,
        List<String> roleCodes,
        List<String> permissionCodes,
        String fingerprint
) {
}
