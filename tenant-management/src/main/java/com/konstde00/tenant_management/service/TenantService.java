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
import com.konstde00.tenant_management.service.data_source.DatasourceConfigService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@DependsOn("dataSourceRouting")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantService {

    static Boolean CREATED = true;
    TenantDao tenantDao;
    UserService userService;
    LiquibaseService liquibaseService;
    TenantRepository tenantRepository;
    DatasourceConfigService datasourceConfigService;
    DataSourceRoutingService dataSourceRoutingService;

    public TenantService(UserService userService,
                         TenantRepository tenantRepository,
                         LiquibaseService liquibaseService,
                         @Qualifier("mainDataSourceProperties") DataSourceProperties mainDatasourceProperties,
                         DatasourceConfigService datasourceConfigService,
                         DataSourceRoutingService dataSourceRoutingService) {
        this.userService = userService;
        this.tenantRepository = tenantRepository;
        this.liquibaseService = liquibaseService;
        this.tenantDao = new TenantDao(mainDatasourceProperties);
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

        requestDto.setUserName(requestDto.getUserName().toLowerCase());

        tenantDao.createTenantDb(requestDto.getName(), requestDto.getUserName(), requestDto.getDbPassword());
        liquibaseService.enableMigrations(requestDto.getDbName(), requestDto.getUserName(), requestDto.getDbPassword());

        Tenant tenant = TenantMapper.INSTANCE.fromRequestDto(requestDto);

        tenant.setDbCreated(CREATED);
        tenant = saveAndFlush(tenant);

        userService.create(requestDto.getUser(), tenant, List.of(Role.ADMIN));

        dataSourceRoutingService.updateResolvedDataSources(datasourceConfigService
                .configureDataSources(tenantDao.getTenantInfo()));

        return TenantMapper.INSTANCE.toResponseDto(tenant);
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
