package com.konstde00.auth.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MainDataSourceConfig {

    @Value("${datasource.main.driver}")
    String driver;

    @Value("${datasource.main.url}")
    String url;

    @Value("${datasource.main.username}")
    String username;

    @Value("${datasource.main.password}")
    String password;

    @Bean(value = "mainDataSource")
    public DataSource dataSource(){

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setDriverClassName(driver);
        ds.setPassword(password);
        ds.setRollbackOnReturn(false);

        return ds;
    }
}
