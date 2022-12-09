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
import lombok.val;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@DependsOn("dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LiquibaseService implements SmartInitializingSingleton {

    TenantService tenantService;
    ConnectionService connectionService;

    public LiquibaseService(@Lazy TenantService tenantService,
                            ConnectionService connectionService) {
        this.tenantService = tenantService;
        this.connectionService = connectionService;
    }

    static String CHANGELOG_FILE = "db.changelog-master.yml";
    static String MAIN_DS_MIGRATIONS_CLASSPATH = "classpath:liquibase/migrations/main_db/";
    static String TENANT_MIGRATIONS_CLASSPATH = "classpath:liquibase/migrations/tenant_db/";

    public void afterSingletonsInstantiated() {

        enableMigrationsToMainDatasource();

        List<Tenant> tenants = tenantService.findAll();

        for (Tenant tenant : tenants) {

            enableMigrations(tenant.getDbName(), tenant.getUserName(), tenant.getDbPassword());
        }
    }

    public synchronized void enableMigrations(String dbName, String userName, String dbPassword) {

        try (Connection connection = connectionService.getConnection(dbName, userName, dbPassword);
             Liquibase liquibase = new Liquibase(TENANT_MIGRATIONS_CLASSPATH + CHANGELOG_FILE,
                     new ClassLoaderResourceAccessor(), getDatabase(connection))) {

            liquibase.update(new Contexts(), new LabelExpression());

        } catch (Exception throwable) {

            log.error("Exception during enabling migrations to main datasource: {}", throwable.getMessage());
        }
    }

    public void enableMigrationsToMainDatasource() {

        try (Connection connection = connectionService.getConnectionToMainDatasource();
             Liquibase liquibase = new Liquibase(MAIN_DS_MIGRATIONS_CLASSPATH + CHANGELOG_FILE,
                     new ClassLoaderResourceAccessor(), getDatabase(connection))) {

            liquibase.update(new Contexts(), new LabelExpression());

        } catch (Throwable throwable) {

            log.error("Exception during enabling tenant migrations: {}", throwable.getMessage());
        }
    }

    private Database getDatabase(Connection connection) throws DatabaseException {

        return DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
    }
}
