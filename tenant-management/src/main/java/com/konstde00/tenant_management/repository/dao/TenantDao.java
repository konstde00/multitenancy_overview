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

    public List<TenantDbInfoDto> getTenantInfo(DatabaseCreationStatus creationStatus) {

        String query = "select id, db_name, user_name, db_password " +
                "from tenants " +
                "where creation_status = :creationStatus";

        MapSqlParameterSource params = new MapSqlParameterSource("creationStatus", creationStatus.getValue());

        return jdbcTemplate.query(query, params, (rs, rowNum) -> {

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

        String createDbQuery = "CREATE DATABASE :dbName";
        MapSqlParameterSource paramsForDbCreation = new MapSqlParameterSource("dbName", dbName);

        jdbcTemplate.update(createDbQuery, paramsForDbCreation);
        log.info("Created database: " + dbName);

        String grantPrivilegesQuery = "GRANT ALL PRIVILEGES ON DATABASE :dbName TO :userName";
        MapSqlParameterSource paramsForGrantingPrivileges = new MapSqlParameterSource("dbName", dbName)
                .addValue("userName", userName);

        jdbcTemplate.update(grantPrivilegesQuery, paramsForGrantingPrivileges);
    }

    private void createUserIfMissing(String userName, String password) {

        try {

            String createUserQuery = """
                DO
                            $do$
                                BEGIN
                                    IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = :userName) THEN
                                       ALTER USER :userName WITH PASSWORD :password;                      ELSE
                                        CREATE USER :userName WITH CREATEDB CREATEROLE PASSWORD :password;
                                    END IF;
                                END
                            $do$""";

            MapSqlParameterSource params = new MapSqlParameterSource("userName", userName)
                    .addValue("password", password);

            jdbcTemplate.update(createUserQuery, params);

        } catch (Exception exception) {

            log.error("Error during creation user : {}", exception.getMessage());
        }
    }
}
