package ru.sbrf.platformmanagement.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Настройки подсистемы login-sync ({@code app.usersync.*}). {@code enabled} позволяет
 * полностью выключить эту функциональность, не трогая ни контроллер, ни сервис логина.
 */
@ConfigurationProperties(prefix = "app.usersync")
public record UserSyncProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("1") int refreshHours,
        @DefaultValue("2") int executorCorePoolSize,
        @DefaultValue("4") int executorMaxPoolSize,
        @DefaultValue("500") int executorQueueCapacity,
        @DefaultValue("100000") long cacheMaximumSize,
        @DefaultValue("") String inboundApiKey
) {
}
