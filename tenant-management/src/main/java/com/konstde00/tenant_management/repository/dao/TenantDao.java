package com.konstde00.tenant_management.repository.dao;

import com.konstde00.tenant_management.domain.dto.data_source.TenantDbInfoDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantDao {

    @NonFinal
    @Value("${datasource.base-url}")
    String datasourceBaseUrl;

    @NonFinal
    @Value("${datasource.main.driver}")
    String mainDatasourceDriverClassName;

    @NonFinal
    @Value("${datasource.main.name}")
    String mainDbName;

    JdbcTemplate jdbcTemplate;

    @Autowired
    public TenantDao(
            @Qualifier("mainDataSource") DataSource mainDataSource) {
        this.jdbcTemplate = new JdbcTemplate(mainDataSource);
    }

    public List<TenantDbInfoDto> getTenantInfo() {

        String query = "select id, db_name, user_name, db_password " +
                "from tenants " +
                "where db_created = true";
        return jdbcTemplate.query(query, (rs, rowNum) -> {

            TenantDbInfoDto dto = new TenantDbInfoDto();

            dto.setKey(rs.getLong("id"));
            dto.setDbName(rs.getString("db_name"));
            dto.setUserName(rs.getString("user_name"));
            dto.setDbPassword(rs.getString("db_password"));

            return dto;
        });
    }

    public void createTenantDb(String dbName, String userName, String password) {

        createUserIfMissing(userName, password);

        String createDbQuery = String.format("CREATE DATABASE %s", dbName);

        jdbcTemplate.execute(createDbQuery);
        log.info("Created database: " + dbName);

        jdbcTemplate.execute(String.format("GRANT ALL PRIVILEGES ON DATABASE %s TO %s", dbName, userName));
    }

    private void createUserIfMissing(String userName, String password) {

        try {

            String createAgentQuery = String.format("""
                DO
                            $do$
                                BEGIN
                                    IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '%s') THEN
                                       ALTER USER %s WITH PASSWORD '%s';                    ELSE
                                        CREATE USER %s WITH CREATEDB CREATEROLE PASSWORD '%s';
                                    END IF;
                                END
                            $do$""", userName, userName, password, userName, password);
            jdbcTemplate.execute(createAgentQuery);

        } catch (Exception exception) {

            log.error("Error during creation user : {}", exception.getMessage());
        }
    }
}
