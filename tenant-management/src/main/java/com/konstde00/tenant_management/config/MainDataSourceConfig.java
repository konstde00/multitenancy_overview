package com.konstde00.tenant_management.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@PropertySource(value = "classpath:config.yml")
public class MainDataSourceConfig {

    @Value("${datasource.main.driver:org.postgresql.Driver}")
    String driver;

    @Value("${datasource.main.url:jdbc:postgresql://localhost:5432/demo_lab}")
    String url;

    @Value("${datasource.main.username:demo_lab}")
    String username;

    @Value("${datasource.main.password:mega_secure_password}")
    String password;

    @Bean(value = "mainDataSourceProperties")
    public DataSourceProperties dataSource(){

        DataSourceProperties ds = new DataSourceProperties();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setDriverClassName(driver);
        ds.setPassword(password);

        return ds;
    }
}
