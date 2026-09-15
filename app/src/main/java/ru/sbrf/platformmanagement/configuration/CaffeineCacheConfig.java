package ru.sbrf.platformmanagement.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class CaffeineCacheConfig {

    public static final String USER_SYNC_FINGERPRINT_CACHE = "userSyncFingerprintCache";

    private final UserSyncProperties properties;

    /**
     * tab_num -> отпечаток последней синхронизации (hex SHA-256). {@code expireAfterWrite}
     * гарантирует "обновление не реже раза в час", даже если у пользователя ничего не
     * поменялось.
     */
    @Bean(USER_SYNC_FINGERPRINT_CACHE)
    public Cache<Long, String> userSyncFingerprintCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(properties.refreshHours()))
                .maximumSize(properties.cacheMaximumSize())
                .build();
    }
}
