package com.konstde00.applicationmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableFeignClients
@DependsOn("dataSourceRouting")
@EntityScan(basePackages = {"com.konstde00.auth", "com.konstde00.commons",
        "com.konstde00.tenant_management", "com.konstde00.lab"})
@EnableJpaRepositories(basePackages = {"com.konstde00.auth", "com.konstde00.commons",
        "com.konstde00.tenant_management", "com.konstde00.lab"},
        transactionManagerRef = "customTransactionManager",
        entityManagerFactoryRef = "customEntityManager")
@PropertySource(value = "file:application/src/main/resources/application.yml")
@SpringBootApplication(
        exclude={
                DataSourceAutoConfiguration.class,
                SqlInitializationAutoConfiguration.class,
                LiquibaseAutoConfiguration.class
        },
    scanBasePackages = {"com.konstde00.auth", "com.konstde00.commons",
            "com.konstde00.tenant_management", "com.konstde00.lab",
            "com.konstde00.applicationmodule"})
public class Application {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }
}
