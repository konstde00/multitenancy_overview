package com.konstde00.admin_module.service;

import com.konstde00.commons.domain.entity.Tenant;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
//@DependsOn("dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LiquibaseService implements SmartInitializingSingleton {

    TenantService tenantService;
    ConnectionService connectionService;

    public LiquibaseService(@Lazy TenantService tenantService,
                            ConnectionService connectionService) {
        this.tenantService = tenantService;
        this.connectionService = connectionService;
    }

    static String CHANGELOG_FILE = "master.xml";
    static String MAIN_DS_MIGRATIONS_CLASSPATH = "classpath:liquibase/main_db_migrations/";
    static String TENANT_MIGRATIONS_CLASSPATH = "classpath:liquibase/tenant_db_migrations/";

    public void afterSingletonsInstantiated() {

        enableMigrationsToMainDatasource();

        List<Tenant> tenants = tenantService.findAll();

        for (Tenant tenant : tenants) {

            enableMigrations(tenant.getDbName(), tenant.getDbPassword());
        }
    }

    public synchronized void enableMigrations(String dbName, String dbPassword) {

        try (Connection connection = connectionService.getConnection(dbName, dbPassword)) {

            Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(TENANT_MIGRATIONS_CLASSPATH + CHANGELOG_FILE,
                new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());

            liquibase.close();

        } catch (Throwable throwable) {

            log.error("Exception during enabling migrations to main datasource: {}", throwable.getMessage());
        }
    }

    public void enableMigrationsToMainDatasource() {


        try (Connection connection = connectionService.getConnectionToMainDatasource()) {

            Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(MAIN_DS_MIGRATIONS_CLASSPATH + CHANGELOG_FILE,
                new ClassLoaderResourceAccessor(), database);
            liquibase.update(new Contexts(), new LabelExpression());

            liquibase.close();

        } catch (Throwable throwable) {

            log.error("Exception during enabling tenant migrations: {}", throwable.getMessage());
        }
    }
}
