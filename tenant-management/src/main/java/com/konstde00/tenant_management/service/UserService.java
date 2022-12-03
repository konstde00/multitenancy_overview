package com.konstde00.tenant_management.service;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.domain.enums.Role;
import com.konstde00.commons.exceptions.ResourceNotFoundException;
import com.konstde00.commons.util.SecurityUtils;
import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import com.konstde00.tenant_management.domain.dto.response.CreateUserResponseDto;
import com.konstde00.tenant_management.mapper.UserMapper;
import com.konstde00.tenant_management.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    @NonFinal
    @Value("${jwt.secret}")
    String jwtSecret;

    TenantService tenantService;
    UserRepository userRepository;

    public UserService(@Lazy TenantService tenantService,
                       UserRepository userRepository) {
        this.tenantService = tenantService;
        this.userRepository = userRepository;
    }

    public User getByEmail(String email) {

        log.info("'getByEmail' invoked with email - {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(format("User with email - %s does not exist. ", email));
                    return new ResourceNotFoundException(format("User with email - %s does not exist. ", email));
                });

        log.info("'getByEmail' returned 'Success'");

        return user;
    }

    public CreateUserResponseDto create(CreateUserRequestDto requestDto, List<Role> roles, HttpServletRequest request) {

        Tenant tenant = tenantService.getById(SecurityUtils.getTenantId(request, jwtSecret));

        return create(requestDto, tenant, roles);
    }

    public CreateUserResponseDto create(CreateUserRequestDto requestDto, Tenant tenant, List<Role> roles) {

        User user = UserMapper.INSTANCE.fromRequestDto(requestDto);

        user.setRoles(roles);
        user.setTenant(tenant);

        user = userRepository.saveAndFlush(user);

        return UserMapper.INSTANCE.toResponseDto(user);
    }
}
