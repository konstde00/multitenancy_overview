package com.konstde00.tenant_management.service;

import com.konstde00.commons.domain.entity.Tenant;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@DependsOn("dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LiquibaseService implements SmartInitializingSingleton {

    DataSource dataSource;
    TenantService tenantService;
    ConnectionService connectionService;

    public LiquibaseService(@Qualifier("mainDataSource") DataSource dataSource,
                            @Lazy TenantService tenantService,
                            ConnectionService connectionService) {
        this.dataSource = dataSource;
        this.tenantService = tenantService;
        this.connectionService = connectionService;
    }

    static String CHANGELOG_FILE = "db.changelog-master.yml";
    static String MAIN_DS_MIGRATIONS_CLASSPATH = "classpath:liquibase/migrations/main_db/";
    static String TENANT_MIGRATIONS_CLASSPATH = "classpath:liquibase/migrations/tenant_db/";

    public void afterSingletonsInstantiated() {

        try {
            enableMigrationsToMainDatasource(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Tenant> tenants = tenantService.findAll();

        for (Tenant tenant : tenants) {

            enableMigrationsToTenantDatasource(tenant.getDbName(), tenant.getUserName(), tenant.getDbPassword());
        }
    }

    public void enableMigrationsToTenantDatasource(String dbName, String userName, String dbPassword) {

        try (Connection connection = connectionService.getConnection(dbName, userName, dbPassword)) {

            enableMigrationsToTenantDatasource(connection);

        } catch (Exception exception) {

            log.error("Exception during enabling migrations to tenant datasource: {}", exception.getMessage());
        }
    }
    public static void enableMigrationsToTenantDatasource(Connection connection) {

        try (Liquibase liquibase = new Liquibase(TENANT_MIGRATIONS_CLASSPATH + CHANGELOG_FILE,
                     new ClassLoaderResourceAccessor(), getDatabase(connection))) {

            liquibase.update(new Contexts(), new LabelExpression());

        } catch (Exception exception) {

            log.error("Exception during enabling migrations to tenant datasource: {}", exception.getMessage());
        }
    }

    public void enableMigrationsToMainDatasource(String dbName, String userName, String dbPassword) {

        try (Connection connection = connectionService.getConnection(dbName, userName, dbPassword)) {

            enableMigrationsToMainDatasource(connection);

        } catch (Exception exception) {

            log.error("Exception during enabling migrations to main datasource: {}", exception.getMessage());
        }
    }

    public static void enableMigrationsToMainDatasource(Connection connection) {

        try (Liquibase liquibase = new Liquibase(MAIN_DS_MIGRATIONS_CLASSPATH + CHANGELOG_FILE,
                     new ClassLoaderResourceAccessor(), getDatabase(connection))) {

            liquibase.update(new Contexts(), new LabelExpression());

        } catch (Exception exception) {

            log.error("Exception during enabling migrations to main datasource: {}", exception.getMessage());
        }
    }

    private static Database getDatabase(Connection connection) throws DatabaseException {

        return DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
    }
}
