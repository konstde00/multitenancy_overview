package com.konstde00.applicationmodule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.konstde00.auth", "com.konstde00.commons",
        "com.konstde00.tenant_management", "com.konstde00.lab"})
@EnableJpaRepositories(basePackages = {"com.konstde00.auth",
        "com.konstde00.tenant_management", "com.konstde00.lab"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
    scanBasePackages = {"com.konstde00.auth", "com.konstde00.commons",
            "com.konstde00.tenant_management", "com.konstde00.lab"})
public class Application {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run(args);
    }
}
