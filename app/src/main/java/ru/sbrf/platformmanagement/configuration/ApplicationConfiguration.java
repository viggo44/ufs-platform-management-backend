package ru.sbrf.platformmanagement.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.sbrf.phoenix.signal.openapi.OpenApiConfiguration;
import ru.sbrf.phoenix.signal.security.SecurityConfiguration;
import ru.sbrf.phoenix.signal.web.servlet.ErrorResolverConfiguration;
import ru.sbrf.phoenix.signal.web.servlet.metrics.JerseyMetricsConfiguration;

@Configuration
@Import({OpenApiConfiguration.class, SecurityConfiguration.class, JerseyMetricsConfiguration.class, ErrorResolverConfiguration.class})
public class ApplicationConfiguration {
}
