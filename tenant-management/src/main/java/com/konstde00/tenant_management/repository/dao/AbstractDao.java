package com.konstde00.tenant_management.repository.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Slf4j
@Data
@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractDao {

    @NonFinal
    @Value("${datasource.base-url:jdbc:postgresql://localhost:5432/}")
    String datasourceBaseUrl;

    @NonFinal
    @Value("${datasource.main.driver:org.postgresql.Driver}")
    String mainDatasourceDriverClassName;

    @NonFinal
    @Value("${datasource.main.name:demo_lab}")
    String mainDbName;

    NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    protected AbstractDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
}
