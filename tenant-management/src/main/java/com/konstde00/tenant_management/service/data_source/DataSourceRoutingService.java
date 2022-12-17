package com.konstde00.tenant_management.service.data_source;

import com.konstde00.tenant_management.domain.dto.data_source.TenantDbInfoDto;
import com.konstde00.tenant_management.repository.dao.TenantDao;
import com.konstde00.tenant_management.service.dao_holder.DaoHolder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.konstde00.commons.domain.enums.DatabaseCreationStatus.CREATED;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RefreshScope
@Service(value = "dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class DataSourceRoutingService extends AbstractRoutingDataSource implements SmartInitializingSingleton {

    TenantDao tenantDao;
    Map<String, DaoHolder> daoHolders;
    DatasourceConfigService datasourceConfigService;

    @NonFinal
    Map<Object, DataSource> resolvedDataSources;

    public DataSourceRoutingService(@Lazy DatasourceConfigService datasourceConfigService,
                                    @Qualifier("mainDataSource") DataSource mainDataSource,
                                    Map<Object, DataSource> resolvedDataSources,
                                    Map<String, DaoHolder> daoHolders) {
        this.datasourceConfigService = datasourceConfigService;
        this.tenantDao = new TenantDao(mainDataSource);

        this.resolvedDataSources = resolvedDataSources;
        this.setTargetDataSources(Map.of());
        this.setDefaultTargetDataSource(mainDataSource);

        this.daoHolders = daoHolders;
    }

    @Override
    public void afterSingletonsInstantiated() {

        List<TenantDbInfoDto> tenantDbInfo = tenantDao.getTenantInfo(CREATED);

        Map<Object, DataSource> dataSources
                = datasourceConfigService.configureDataSources(tenantDbInfo);

        updateDaoTemplateHolders(dataSources);
    }

    @Override
    public Map<Object, DataSource> getResolvedDataSources() {

        return Collections.unmodifiableMap(this.resolvedDataSources);
    }

    @Override
    protected Object determineCurrentLookupKey() {

        return DataSourceContextHolder.getCurrentTenantId();
    }

    public void updateResolvedDataSources(Map<Object, DataSource> dataSources) {

        resolvedDataSources = dataSources;

        updateDaoTemplateHolders(dataSources);
    }

    public void updateDaoTemplateHolders(Map<Object, DataSource> dataSources) {

        daoHolders.forEach((key, value) -> value.addNewTemplates(dataSources));
    }
}
