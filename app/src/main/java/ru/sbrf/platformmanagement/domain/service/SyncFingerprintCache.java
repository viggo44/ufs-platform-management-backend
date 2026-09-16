package ru.sbrf.platformmanagement.domain.service;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Тонкая обёртка над Caffeine-кэшем {@code tab_num -> отпечаток последней синхронизации}
 * ({@code expireAfterWrite(1ч)}, см. {@code CaffeineCacheConfig}). Один TTL уже даёт
 * "синхронизацию не реже раза в час" (промах кэша заставляет пересинхронизировать);
 * сравнение отпечатков при попадании в кэш даёт "синхронизацию сразу, если что-то
 * изменилось, не дожидаясь часа".
 */
@Component
@RequiredArgsConstructor
public class SyncFingerprintCache {

    private final Cache<String, String> cache;

    public boolean isUpToDate(String tabNum, String fingerprint) {
        String cached = cache.getIfPresent(tabNum);
        return cached != null && cached.equals(fingerprint);
    }

    public void markSynced(String tabNum, String fingerprint) {
        cache.put(tabNum, fingerprint);
    }
}
