package com.konstde00.tenant_management.service.dao_holder.impl;

import com.konstde00.tenant_management.repository.dao.TenantDao;
import com.konstde00.tenant_management.service.dao_holder.DaoHolder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TenantDaoHolderImpl implements DaoHolder {

    @NonFinal
    Map<Long, TenantDao> templates;

    @NonFinal
    @Value("${datasource.base-url:jdbc:postgresql://localhost:5432/}")
    String datasourceBaseUrl;

    @NonFinal
    @Value("${datasource.main.driver:org.postgresql.Driver}")
    String mainDatasourceDriverClassName;

    @NonFinal
    @Value("${datasource.main.name:demo_data_center}")
    String mainDbName;

    @Override
    public void afterSingletonsInstantiated() {

        templates = new HashMap<>();
    }

    public TenantDao getTemplateByTenantKey(Long tenantKey) {

        return templates.get(tenantKey);
    }

    public void addNewTemplates(Map<Object, DataSource> dataSources) {

        dataSources.forEach((key, value) -> templates.putIfAbsent((Long) key,
            TenantDao.builder()
                .jdbcTemplate(new JdbcTemplate(value))
                .datasourceBaseUrl(datasourceBaseUrl)
                .mainDatasourceDriverClassName(mainDatasourceDriverClassName)
                .mainDbName(mainDbName)
                .build()));
    }
}
