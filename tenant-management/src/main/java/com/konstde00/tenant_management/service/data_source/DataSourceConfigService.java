package com.konstde00.tenant_management.service.data_source;

import com.konstde00.tenant_management.domain.dto.data_source.TenantDbInfoDto;
import com.konstde00.tenant_management.repository.dao.TenantDao;
import com.konstde00.tenant_management.service.LiquibaseService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.konstde00.commons.domain.enums.DatabaseCreationStatus.CREATED;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataSourceConfigService {

    @NonFinal
    @Value("${datasource.main.name}")
    String mainDatasourceName;

    @NonFinal
    @Value("${datasource.main.username}")
    String mainDatasourceUsername;

    @NonFinal
    @Value("${datasource.main.password}")
    String mainDatasourcePassword;

    @NonFinal
    @Value("${datasource.base-url}")
    String datasourceBaseUrl;

    @NonFinal
    Boolean wasMainDatasourceConfigured = false;

    DataSource mainDataSource;
    LiquibaseService liquibaseService;

    public DataSourceConfigService(@Qualifier("mainDataSource") DataSource mainDataSource,
                                   LiquibaseService liquibaseService) {
        this.mainDataSource = mainDataSource;
        this.liquibaseService = liquibaseService;
    }

    public Map<Object, Object> configureDataSources() {

        Map<Object, Object> dataSources = new HashMap<>();

        if (!wasMainDatasourceConfigured)  {
            liquibaseService.enableMigrationsToMainDatasource(mainDatasourceName,
                    mainDatasourceUsername, mainDatasourcePassword);
            wasMainDatasourceConfigured = true;
        }

        List<TenantDbInfoDto> dtos = new TenantDao(mainDataSource).getTenantDbInfo(CREATED);

        dataSources.put(null, mainDataSource);
        for (TenantDbInfoDto dto : dtos) {

            dataSources.put(dto.getId(), configureDataSource(dto));
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
