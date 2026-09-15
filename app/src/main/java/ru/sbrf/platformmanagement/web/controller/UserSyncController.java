package ru.sbrf.platformmanagement.web.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sbrf.platformmanagement.domain.model.UserSnapshot;
import ru.sbrf.platformmanagement.domain.service.UserSnapshotFactory;
import ru.sbrf.platformmanagement.domain.service.UserSyncService;
import ru.sbrf.platformmanagement.security.InternalApi;
import ru.sbrf.platformmanagement.web.dto.UserSyncRequest;

/**
 * Точка входа для сервиса логина: он шлёт сюда HTTP-запрос сразу после успешного входа,
 * с уже готовым снимком пользователя (id, ФИО, роли, пермишены) — без обратного вызова
 * за данными, у нас нет доступа к его read-API. Не относится к frontend-контракту, поэтому
 * DTO ({@code UserSyncRequest}) лежит в {@code web.dto}, а не в модуле {@code api}.
 *
 * <p><b>Производительность — это весь смысл этого класса.</b> Ответ уходит немедленно
 * (202, без ожидания записи в БД); дорогая часть (апсерт) всегда уходит в
 * {@link UserSyncService#syncAsync} на отдельный пул. Ни бракованный снимок, ни
 * переполненный пул, ни упавшая транзакция не должны заставить сервис логина ждать
 * дольше, чем один Caffeine-lookup.
 */
@Path("/internal/usersync")
@Produces(MediaType.APPLICATION_JSON)
@InternalApi
@Component
@RequiredArgsConstructor
@Slf4j
public class UserSyncController {

    private final UserSnapshotFactory snapshotFactory;
    private final UserSyncService userSyncService;

    @POST
    public Response sync(@Valid UserSyncRequest request) {
        try {
            UserSnapshot snapshot = snapshotFactory.from(request);
            userSyncService.syncAsync(snapshot);
        } catch (Exception e) {
            // Даже сбой на нашей стороне не должен обернуться ошибкой для вызывающего
            // сервиса логина — следующий логин повторит попытку.
            log.error("Failed to accept login-sync payload for SUDIR user [{}]", request.getUserId(), e);
        }
        return Response.status(Response.Status.ACCEPTED).build();
    }
}
