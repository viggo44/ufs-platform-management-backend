package ru.sbrf.platformmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.sbrf.phoenix.signal.web.servlet.BaseErrorResolver;
import ru.sbrf.phoenix.signal.web.WebConstants;
import ru.sbrf.platformmanagement.authentication.PromFilterConfiguration;
import ru.sbrf.platformmanagement.authentication.StubFilterConfiguration;
import ru.sbrf.ufs.platform.authentication.annotations.v1.AuthenticationConfiguration;
import ru.sbrf.ufs.platform.core.annotation.RestApp;

@SpringBootApplication(exclude = {JmsAutoConfiguration.class, ValidationAutoConfiguration.class})
@AuthenticationConfiguration(profile = "PROM", value = PromFilterConfiguration.class)
@AuthenticationConfiguration(profile = "STUB", value = StubFilterConfiguration.class)
@RestApp(url = "/api/v1/*",
        packageToScan = {"ru.sbrf.platformmanagement.web", WebConstants.SIGNAL_WEB_MODULE_PACKAGE},
        errorResolverClass = BaseErrorResolver.class)
@ConfigurationPropertiesScan
public class PlatformManagement {

    public static void main(String[] args) {
        SpringApplication.run(PlatformManagement.class, args);
    }
}
