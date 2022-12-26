package com.konstde00.tenant_management.service;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.commons.domain.enums.Role;
import com.konstde00.commons.exceptions.NotValidException;
import com.konstde00.tenant_management.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.request.RenameTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.response.TenantResponseDto;
import com.konstde00.tenant_management.mapper.TenantMapper;
import com.konstde00.tenant_management.repository.TenantRepository;
import com.konstde00.tenant_management.repository.dao.TenantDao;
import com.konstde00.tenant_management.service.data_source.DataSourceRoutingService;
import com.konstde00.tenant_management.service.data_source.DataSourceConfigService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static com.konstde00.commons.domain.enums.DatabaseCreationStatus.*;

@Slf4j
@Service
@DependsOn("dataSourceRouting")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantService {

    TenantDao tenantDao;
    UserService userService;
    LiquibaseService liquibaseService;
    TenantRepository tenantRepository;
    DataSourceConfigService datasourceConfigService;
    DataSourceRoutingService dataSourceRoutingService;

    public TenantService(UserService userService,
                         TenantRepository tenantRepository,
                         LiquibaseService liquibaseService,
                         @Qualifier("mainDataSource") DataSource mainDatasource,
                         DataSourceConfigService datasourceConfigService,
                         DataSourceRoutingService dataSourceRoutingService) {
        this.userService = userService;
        this.tenantRepository = tenantRepository;
        this.liquibaseService = liquibaseService;
        this.tenantDao = new TenantDao(mainDatasource);
        this.datasourceConfigService = datasourceConfigService;
        this.dataSourceRoutingService = dataSourceRoutingService;
    }

    public Tenant getById(Long id) {

        return tenantRepository.findById(id)
                .orElseThrow(() -> new NotValidException("Can't find tenant by id " + id));
    }

    public List<Tenant> findAll() {

        return tenantRepository.findAll();
    }

    public TenantResponseDto create(CreateTenantRequestDto requestDto) {

        Tenant tenant = TenantMapper.INSTANCE.fromRequestDto(requestDto);

        tenant.setCreationStatus(IN_PROGRESS);
        tenant = saveAndFlush(tenant);

        try {

            tenantDao.createTenantDb(requestDto.getName(), requestDto.getUserName(), requestDto.getDbPassword());
            tenant.setCreationStatus(CREATED);

        } catch (Exception e) {

            log.error("Failed to create tenant db: " + e.getMessage());
            tenant.setCreationStatus(FAILED_TO_CREATE);

        } finally {

            tenant = saveAndFlush(tenant);
        }

        TenantResponseDto responseDto = TenantMapper.INSTANCE.toResponseDto(tenant);

        if (CREATED.equals(tenant.getCreationStatus())) {

            liquibaseService.enableMigrationsToTenantDatasource(requestDto.getDbName(), requestDto.getUserName(), requestDto.getDbPassword());

            Long userId = userService.create(requestDto.getUser(), tenant, List.of(Role.ADMIN)).getId();
            responseDto.setUserId(userId);

            Map<Object, Object> configuredDataSources = datasourceConfigService
                    .configureDataSources();

            dataSourceRoutingService.updateResolvedDataSources(configuredDataSources);
        }

        return responseDto;
    }

    public Tenant saveAndFlush(Tenant tenant) {

        return tenantRepository.saveAndFlush(tenant);
    }

    @Transactional
    public void rename(RenameTenantRequestDto params) {

        tenantRepository.rename(params.getId(), params.getName());
    }

    @Transactional
    public void delete(Long id) {

        tenantRepository.deleteById(id);
    }
}
