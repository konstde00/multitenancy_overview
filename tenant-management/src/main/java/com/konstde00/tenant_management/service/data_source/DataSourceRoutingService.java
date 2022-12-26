package com.konstde00.tenant_management.service.data_source;

import com.konstde00.tenant_management.service.LiquibaseService;
import com.konstde00.tenant_management.service.dao_holder.AbstractDaoHolder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RefreshScope
@Service(value = "dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DataSourceRoutingService extends AbstractRoutingDataSource implements SmartInitializingSingleton {

    LiquibaseService liquibaseService;
    Map<String, AbstractDaoHolder> daoHolders;
    DataSourceConfigService datasourceConfigService;

    @NonFinal
    @Value("${datasource.main.name}")
    String mainDatasourceName;

    @NonFinal
    @Value("${datasource.main.username}")
    String mainDatasourceUsername;

    @NonFinal
    @Value("${datasource.main.password}")
    String mainDatasourcePassword;

    public DataSourceRoutingService(@Lazy DataSourceConfigService datasourceConfigService,
                                    LiquibaseService liquibaseService,
                                    @Qualifier("mainDataSource") DataSource mainDataSource,
                                    Map<String, AbstractDaoHolder> daoHolders) {
        this.datasourceConfigService = datasourceConfigService;

        this.liquibaseService = liquibaseService;
        this.liquibaseService.enableMigrationsToMainDatasource(mainDatasourceName,
                mainDatasourceUsername, mainDatasourcePassword);

        Map<Object, Object> dataSourceMap = this.datasourceConfigService.configureDataSources();

        this.setTargetDataSources(dataSourceMap);
        this.setDefaultTargetDataSource(mainDataSource);

        this.daoHolders = daoHolders;
    }

    @Override
    public void afterSingletonsInstantiated() {

        Map<Object, Object> dataSources
                = datasourceConfigService.configureDataSources();

        updateResolvedDataSources(dataSources);

        updateDaoTemplateHolders(dataSources);
    }

    @Override
    protected Long determineCurrentLookupKey() {

        return DataSourceContextHolder.getCurrentTenantId();
    }

    public void updateResolvedDataSources(Map<Object, Object> dataSources) {

        setTargetDataSources(dataSources);

        afterPropertiesSet();
    }

    public void updateDaoTemplateHolders(Map<Object, Object> dataSources) {

        daoHolders.forEach((key, value) -> value.addNewTemplates(dataSources));
    }
}
