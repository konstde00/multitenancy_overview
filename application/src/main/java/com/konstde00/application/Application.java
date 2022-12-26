package com.konstde00.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients
@DependsOn("dataSourceRouting")
@EntityScan(basePackages = {"com.konstde00.auth", "com.konstde00.commons",
        "com.konstde00.tenant_management", "com.konstde00.lab", "com.konstde00.application"})
@EnableJpaRepositories(basePackages = {"com.konstde00.auth", "com.konstde00.commons",
        "com.konstde00.tenant_management", "com.konstde00.lab", "com.konstde00.application"},
        transactionManagerRef = "customTransactionManager",
        entityManagerFactoryRef = "customEntityManager")
@SpringBootApplication(
        exclude={
                DataSourceAutoConfiguration.class,
                SqlInitializationAutoConfiguration.class,
                LiquibaseAutoConfiguration.class
        },
        scanBasePackages = {"com.konstde00.auth", "com.konstde00.commons",
                "com.konstde00.tenant_management", "com.konstde00.lab",
                "com.konstde00.application"})
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }
}
