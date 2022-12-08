package com.konstde00.tenant_management.service.data_source;

import com.konstde00.tenant_management.domain.dto.data_source.TenantDbInfoDto;
import com.konstde00.tenant_management.repository.dao.TenantDao;
import com.konstde00.tenant_management.service.LiquibaseService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DatasourceConfigService {

    @NonFinal
    @Value("${datasource.base-url:jdbc:postgresql://localhost:5432/}")
    String datasourceBaseUrl;

    @NonFinal
    Boolean wasMainDatasourceConfigured = false;

    LiquibaseService liquibaseService;

    public DatasourceConfigService(LiquibaseService liquibaseService) {
        this.liquibaseService = liquibaseService;
    }

    public Map<Object, DataSource> configureDataSources(List<TenantDbInfoDto> dtos) {

        Map<Object, DataSource> dataSources = new HashMap<>();

        if (!wasMainDatasourceConfigured)  {
            liquibaseService.enableMigrationsToMainDatasource();
            wasMainDatasourceConfigured = true;
        }

        for (TenantDbInfoDto dto : dtos) {

            dataSources.put(dto.getKey(), configureDataSource(dto));
        }

        return dataSources;
    }

    private DataSource configureDataSource(TenantDbInfoDto dto) {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setUrl(getUrl(dto));
        dataSource.setUsername(dto.getUserName());
        dataSource.setPassword(dto.getDbPassword());

        return dataSource;
    }

    private String getUrl(TenantDbInfoDto dto) {

        return datasourceBaseUrl + dto.getDbName();
    }
}
