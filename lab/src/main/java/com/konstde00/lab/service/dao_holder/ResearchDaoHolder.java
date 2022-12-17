package com.konstde00.lab.service.dao_holder;

import com.konstde00.tenant_management.repository.dao.TenantDao;
import com.konstde00.tenant_management.service.dao_holder.AbstractDaoHolder;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ResearchDaoHolder extends AbstractDaoHolder {

    @Override
    public void afterSingletonsInstantiated() {

        templates = new HashMap<>();
    }

    public TenantDao getTemplateByTenantKey(Long tenantKey) {

        return templates.get(tenantKey);
    }

    public void addNewTemplates(Map<Object, DataSource> dataSources) {

        dataSources.forEach((key, value) -> {

            TenantDao tenantDao = new TenantDao(value);

            tenantDao.setMainDbName(mainDbName);
            tenantDao.setDatasourceBaseUrl(datasourceBaseUrl);
            tenantDao.setMainDatasourceDriverClassName(mainDatasourceDriverClassName);

            templates.putIfAbsent((Long) key, tenantDao);
        });
    }
}
