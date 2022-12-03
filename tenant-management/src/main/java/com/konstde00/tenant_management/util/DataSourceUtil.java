package com.konstde00.tenant_management.util;

import com.konstde00.commons.domain.entity.Tenant;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;

@Slf4j
@UtilityClass
public class DataSourceUtil {

    public String createDbName(String tenantName) {

        return tenantName;
    }

    public DataSource getDataSource(String datasourceBaseUrl, String driver, String dbName, String userName, String password) {

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(datasourceBaseUrl + dbName);
        ds.setUsername(userName);
        ds.setDriverClassName(driver);
        ds.setPassword(password);
        ds.setRollbackOnReturn(false);

        return ds;
    }
}
