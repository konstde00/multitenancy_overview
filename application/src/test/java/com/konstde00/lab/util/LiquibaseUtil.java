package com.konstde00.lab.util;

import com.konstde00.tenant_management.service.ConnectionService;
import com.konstde00.tenant_management.service.LiquibaseService;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;

@Slf4j
public class LiquibaseUtil {

    public static void enableMigrationsToMainDatasource(PostgreSQLContainer container) {

        try (Connection connection = ConnectionService.getConnection(container)) {

            LiquibaseService.enableMigrationsToMainDatasource(connection);

        } catch (Exception exception) {

            log.error("Exception during enabling main migrations: {}", exception.getMessage());
        }
    }
}
