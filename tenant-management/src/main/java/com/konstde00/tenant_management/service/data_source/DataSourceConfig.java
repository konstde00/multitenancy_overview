package com.konstde00.tenant_management.service.data_source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@DependsOn("dataSourceRouting")
public class DataSourceConfig {

    private DataSourceRoutingService dataSourceRouting;

    public DataSourceConfig(DataSourceRoutingService dataSourceRouting) {
        this.dataSourceRouting = dataSourceRouting;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return dataSourceRouting;
    }

    @Primary
    @Bean(name="customEntityManager")
    public LocalContainerEntityManagerFactoryBean entityManagerBean(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource()).packages("com.konstde00.auth", "com.konstde00.commons",
                "com.konstde00.tenant_management", "com.konstde00.lab", "com.konstde00.applicationmodule").build();
    }

    @Bean(name="entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource()).packages("com.konstde00.auth", "com.konstde00.commons",
                "com.konstde00.tenant_management", "com.konstde00.lab", "com.konstde00.applicationmodule").build();
    }

    @Bean(name = "customTransactionManager")
    public JpaTransactionManager transactionManager(
        @Autowired @Qualifier("customEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }
}
