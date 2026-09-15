package ru.sbrf.platformmanagement.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.sbrf.platformmanagement.configuration.AsyncExecutorConfig;
import ru.sbrf.platformmanagement.configuration.UserSyncProperties;
import ru.sbrf.platformmanagement.domain.model.UserSnapshot;

/**
 * Точка входа, которой {@code UserSyncController} передаёт уже построенный
 * {@link UserSnapshot} (снимок приходит по HTTP от сервиса логина — своей БД у него нет).
 * Выполняется в {@link AsyncExecutorConfig#USER_SYNC_EXECUTOR} — никогда в потоке
 * HTTP-запроса, чтобы наша недоступность/медленность не превращалась в задержку чужого
 * login-флоу.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserSyncService {

    private final SyncFingerprintCache fingerprintCache;
    private final UserSyncWriter userSyncWriter;
    private final UserSyncProperties properties;

    @Async(AsyncExecutorConfig.USER_SYNC_EXECUTOR)
    public void syncAsync(UserSnapshot snapshot) {
        if (!properties.enabled()) {
            return;
        }
        Long tabNum = snapshot.employeeNumberParsed();
        if (tabNum == null) {
            // app_user.tab_num — не null PK; без распарсенного табельного номера писать некуда.
            log.warn("Skipping login-sync for SUDIR user [{}]: employeeNumber [{}] is not a valid tab_num",
                    snapshot.userId(), snapshot.employeeNumberRaw());
            return;
        }
        if (fingerprintCache.isUpToDate(tabNum, snapshot.fingerprint())) {
            return; // в пределах TTL-окна ничего не изменилось — 0 запросов к БД
        }
        try {
            userSyncWriter.upsert(snapshot);
            fingerprintCache.markSynced(tabNum, snapshot.fingerprint());
        } catch (Exception e) {
            // При ошибке кэш не трогаем, чтобы следующий логин повторил синхронизацию полностью.
            log.error("Login-sync failed for tab_num [{}]; will retry on next login", tabNum, e);
        }
    }
}
