package com.konstde00.lab.config;

import com.konstde00.commons.domain.enums.Role;
import com.konstde00.lab.controller.AbstractApiTest;
import com.konstde00.tenant_management.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import com.konstde00.tenant_management.service.TenantService;
import com.konstde00.tenant_management.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import java.util.List;

import static com.konstde00.lab.util.DatabaseContainerInitializer.postgresMainContainer;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestTenantConfig implements SmartInitializingSingleton {

    UserService userService;
    TenantService tenantService;

    public static Long userId;
    public static Long adminId;

    @Override
    public void afterSingletonsInstantiated() {

        var createUserDto = CreateUserRequestDto
                .builder()
                .email("user@domain.com")
                .password("passwordOfUser")
                .build();

        var dto = CreateTenantRequestDto
                .builder()
                .name("tenant")
                .dbName("tenant")
                .dbPassword("mega_secure_password")
                .userName("tenant")
                .user(createUserDto)
                .build();

        var createdTenant = tenantService.create(dto);

        userId = createdTenant.getUserId();
        AbstractApiTest.USER.setId(userId);

        var createAdminDto = CreateUserRequestDto
                .builder()
                .email("admin@domain.com")
                .password("passwordOfAdmin")
                .tenantId(createdTenant.getId())
                .roles(List.of(Role.ADMIN))
                .build();

        adminId = userService.create(createAdminDto).getId();
        AbstractApiTest.ADMIN.setId(adminId);
    }

    @Bean(value = "tenantDataSource")
    public DataSource tenantDataSource(){

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(postgresMainContainer.getJdbcUrl()
                .replace(postgresMainContainer.getDatabaseName(), StringUtils.EMPTY) + "tenant");
        ds.setUsername("tenant");
        ds.setDriverClassName(postgresMainContainer.getDriverClassName());
        ds.setPassword("mega_secure_password");
        ds.setRollbackOnReturn(false);

        return ds;
    }
}
