package com.konstde00.lab.util;

import com.github.dockerjava.api.model.RestartPolicy;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatabaseContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    static String TESTCONTAINERS_DISABLE_PROPERTY = "testcontainers.disable";

    public static PostgreSQLContainer postgresMainContainer
            = new PostgreSQLContainer<>("postgres:14.1-alpine")
            .withUsername("demo_lab")
            .withPassword("mega_secure_password")
            .withDatabaseName("demo_lab")
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withRestartPolicy(RestartPolicy.alwaysRestart()))
            .withLabel("group", "demo_lab");

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (isTestcontainersDisabled(configurableApplicationContext)) {
            return;
        }

        postgresMainContainer.start();

        TestPropertyValues.of(
                "spring.datasource.url=" + postgresMainContainer.getJdbcUrl(),
                "spring.datasource.password=" + postgresMainContainer.getPassword(),
                "spring.datasource.username=" + postgresMainContainer.getUsername(),
                "datasource.main.name=" + postgresMainContainer.getDatabaseName(),
                "datasource.main.password=" + postgresMainContainer.getPassword(),
                "datasource.main.url=" + postgresMainContainer.getJdbcUrl(),
                "datasource.base-url=" + postgresMainContainer.getJdbcUrl()
                        .replace(postgresMainContainer.getDatabaseName(), "")
        ).applyTo(configurableApplicationContext.getEnvironment());

        LiquibaseUtil.enableMigrationsToMainDatasource(DatabaseContainerInitializer.postgresMainContainer);
    }

    private boolean isTestcontainersDisabled(ConfigurableApplicationContext configurableApplicationContext) {
        Boolean testcontainersDisabled = configurableApplicationContext.getEnvironment().getProperty(TESTCONTAINERS_DISABLE_PROPERTY, Boolean.class);
        return Boolean.TRUE.equals(testcontainersDisabled);
    }
}
