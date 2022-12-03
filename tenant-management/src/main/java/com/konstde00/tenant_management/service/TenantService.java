package com.konstde00.tenant_management.service;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.tenant_management.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.request.RenameTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.response.TenantResponseDto;
import com.konstde00.tenant_management.mapper.TenantMapper;
import com.konstde00.tenant_management.repository.TenantRepository;
import com.konstde00.tenant_management.repository.dao.TenantDao;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantService {

    static Boolean CREATED = true;
    TenantDao tenantDao;
    UserService userService;
    LiquibaseService liquibaseService;
    TenantRepository tenantRepository;

    public TenantService(UserService userService,
                         TenantRepository tenantRepository,
                         LiquibaseService liquibaseService,
                         @Qualifier("mainDataSource") DataSource dataSource) {
        this.userService = userService;
        this.tenantRepository = tenantRepository;
        this.liquibaseService = liquibaseService;
        this.tenantDao = new TenantDao(dataSource);
    }

    public List<Tenant> findAll() {

        return tenantRepository.findAll();
    }

    public TenantResponseDto create(CreateTenantRequestDto requestDto) {

        tenantDao.createTenantDb(requestDto.getName(), requestDto.getUserName(), requestDto.getDbPassword());
        liquibaseService.enableMigrations(requestDto.getDbName(), requestDto.getDbPassword());

        Tenant tenant = TenantMapper.INSTANCE.fromRequestDto(requestDto);

        tenant.setDbCreated(CREATED);
        tenant = saveAndFlush(tenant);

        userService.createUser(requestDto.getUser(), tenant);

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
