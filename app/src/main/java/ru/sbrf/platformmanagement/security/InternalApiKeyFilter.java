package ru.sbrf.platformmanagement.security;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sbrf.platformmanagement.configuration.UserSyncProperties;

/**
 * ЗАГЛУШКА межсервисной аутентификации: разделяемый секрет в заголовке. Годится только
 * как временное решение — перед реальной интеграцией заменить на mTLS / OAuth2
 * client-credentials / KMAZ-авторизацию / сетевую политику, в зависимости от того, что уже
 * принято на платформе между сервисами (в этом решении используется KMAZ/ufs-security для
 * фронтового периметра — не проверено, покрывает ли он и межсервисные вызовы тоже).
 * По умолчанию ключ не задан ({@code ""}), поэтому эндпоинт заблокирован, пока его явно
 * не сконфигурируют.
 */
@Provider
@InternalApi
@Component
@RequiredArgsConstructor
public class InternalApiKeyFilter implements ContainerRequestFilter {

    private static final String HEADER = "X-Internal-Api-Key";

    private final UserSyncProperties properties;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String provided = requestContext.getHeaderString(HEADER);
        String expected = properties.inboundApiKey();
        if (expected == null || expected.isBlank() || !expected.equals(provided)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing or invalid " + HEADER)
                    .build());
        }
    }
}
