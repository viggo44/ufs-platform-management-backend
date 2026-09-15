package ru.sbrf.platformmanagement.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncExecutorConfig {

    public static final String USER_SYNC_EXECUTOR = "userSyncExecutor";

    private final UserSyncProperties properties;

    /**
     * Ограниченный пул для работы login-sync. {@link ThreadPoolExecutor.AbortPolicy} (а не
     * {@code CallerRunsPolicy}) выбрана намеренно: при переполнении синхронизацию нужно
     * отбросить, а не выполнять на вызывающем (HTTP) потоке — потерянная синхронизация
     * безвредна, так как следующий логин её повторит.
     */
    @Bean(USER_SYNC_EXECUTOR)
    public ThreadPoolTaskExecutor userSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.executorCorePoolSize());
        executor.setMaxPoolSize(properties.executorMaxPoolSize());
        executor.setQueueCapacity(properties.executorQueueCapacity());
        executor.setThreadNamePrefix("user-sync-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}
