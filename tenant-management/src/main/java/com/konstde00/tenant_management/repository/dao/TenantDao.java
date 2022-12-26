package com.konstde00.tenant_management.repository.dao;

import com.konstde00.commons.domain.enums.DatabaseCreationStatus;
import com.konstde00.tenant_management.domain.dto.data_source.TenantDbInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class TenantDao extends AbstractDao {

    @Autowired
    public TenantDao(@Qualifier("mainDataSource") DataSource mainDataSource) {
        super(mainDataSource);
    }

    public List<TenantDbInfoDto> getTenantDbInfo(DatabaseCreationStatus creationStatus) {

        String query = "select id, db_name, user_name, db_password " +
                "from tenants " +
                "where creation_status = :creationStatus";

        MapSqlParameterSource params = new MapSqlParameterSource("creationStatus", creationStatus.getValue());

        return namedParameterJdbcTemplate.query(query, params, (rs, rowNum) -> {

            TenantDbInfoDto dto = new TenantDbInfoDto();

            dto.setId(rs.getLong("id"));
            dto.setDbName(rs.getString("db_name"));
            dto.setUserName(rs.getString("user_name"));
            dto.setDbPassword(rs.getString("db_password"));

            return dto;
        });
    }

    public void createTenantDb(String dbName, String userName, String password) {

        createUserIfMissing(userName, password);

        String createDbQuery = "CREATE DATABASE " + dbName;

        jdbcTemplate.execute(createDbQuery);
        log.info("Created database: " + dbName);

        String grantPrivilegesQuery = String.format("GRANT ALL PRIVILEGES ON DATABASE %s TO \"%s\"", dbName, userName);

        jdbcTemplate.execute(grantPrivilegesQuery);
    }

    private void createUserIfMissing(String userName, String password) {

        try {

            String createUserQuery = String.format("""
                DO
                            $do$
                                BEGIN
                                    IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '%s') THEN
                                       ALTER USER "%s" WITH PASSWORD '%s';                      ELSE
                                        CREATE USER "%s" WITH CREATEDB CREATEROLE PASSWORD '%s';
                                    END IF;
                                END
                            $do$""", userName, userName, password, userName, password);

            jdbcTemplate.execute(createUserQuery);

        } catch (Exception exception) {

            log.error("Error during creation user : {}", exception.getMessage());
        }
    }
}
