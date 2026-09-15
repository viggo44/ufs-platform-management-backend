package ru.sbrf.platformmanagement.security;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает ресурсы, доступные только другим сервисам платформы (не публичным клиентам
 * frontend-API) — на них навешивается проверка {@link InternalApiKeyFilter}.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface InternalApi {
}
