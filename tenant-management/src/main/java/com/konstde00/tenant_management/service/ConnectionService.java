package com.konstde00.tenant_management.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConnectionService {

    @NonFinal
    @Value("${datasource.main.url:jdbc:postgresql://localhost:5432/demo_lab}")
    String mainDatasourceUrl;

    @NonFinal
    @Value("${datasource.main.username:demo_lab}")
    String mainDatasourceUsername;

    @NonFinal
    @Value("${datasource.main.password:mega_secure_password}")
    String mainDatasourcePassword;

    @NonFinal
    @Value("${datasource.base-url:jdbc:postgresql://localhost:5432/}")
    String datasourceBaseUrl;

    @NonFinal
    @Value("${datasource.main.driver:org.postgresql.Driver}")
    String mainDatasourceDriverClassName;

    static String USER = "user";
    static String PASSWORD = "password";

    public Connection getConnectionToMainDatasource() throws ConnectException {

        try {
            Properties dbProperties = new Properties();
            Class.forName(mainDatasourceDriverClassName);
            dbProperties.put(USER, mainDatasourceUsername);
            dbProperties.put(PASSWORD, mainDatasourcePassword);
            return DriverManager.getConnection(mainDatasourceUrl, dbProperties);
        } catch (SQLException | ClassNotFoundException e) {

            log.error(e.getMessage());

            throw new ConnectException("Can't connect to DB " + mainDatasourceUrl);
        }
    }

    public Connection getConnection(String dbName, String userName, String dbPassword) throws ConnectException {

        try {

            Properties dbProperties = new Properties();
            Class.forName(mainDatasourceDriverClassName);
            dbProperties.put(USER, userName);
            dbProperties.put(PASSWORD, dbPassword);

            return DriverManager
                .getConnection(datasourceBaseUrl + dbName,
                    dbProperties);

        } catch (SQLException | ClassNotFoundException e) {

            log.error(e.getMessage());

            throw new ConnectException("Can't connect to DB");
        }
    }
}
