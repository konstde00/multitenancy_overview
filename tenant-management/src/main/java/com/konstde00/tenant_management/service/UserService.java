package com.konstde00.tenant_management.service;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.domain.enums.Role;
import com.konstde00.commons.exceptions.ForbiddenException;
import com.konstde00.commons.exceptions.ResourceNotFoundException;
import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import com.konstde00.tenant_management.domain.dto.response.CreateUserResponseDto;
import com.konstde00.tenant_management.domain.dto.response.UserAuthShortDto;
import com.konstde00.tenant_management.mapper.UserMapper;
import com.konstde00.tenant_management.repository.UserRepository;
import com.konstde00.tenant_management.repository.dao.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@DependsOn("dataSourceRouting")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    @NonFinal
    @Value("${jwt.secret:very_strong_secret_very_strong_secret_very_strong_secret_very_strong_secret_very_strong_secret_very_strong_secret}")
    String jwtSecret;

    UserDao userDao;
    TenantService tenantService;
    UserRepository userRepository;

    public UserService(UserDao userDao,
                       @Lazy TenantService tenantService,
                       UserRepository userRepository) {
        this.userDao = userDao;
        this.tenantService = tenantService;
        this.userRepository = userRepository;
    }

    public User getByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(format("User with email - %s does not exist. ", email));
                    return new ResourceNotFoundException(format("User with email - %s does not exist. ", email));
                });
    }

    public CreateUserResponseDto create(CreateUserRequestDto requestDto) {

        Tenant tenant = tenantService.getById(requestDto.getTenantId());

        return create(requestDto, tenant, requestDto.getRoles());
    }

    public CreateUserResponseDto create(CreateUserRequestDto requestDto, Tenant tenant, List<Role> roles) {

        User user = UserMapper.INSTANCE.fromRequestDto(requestDto);

        user.setRoles(roles);
        user.setTenant(tenant);
        user.setPassword(bcryptPassword(requestDto.getPassword()));

        user = userRepository.saveAndFlush(user);

        return UserMapper.INSTANCE.toResponseDto(user);
    }

    private String bcryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Transactional
    public UserAuthShortDto getActualUser(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);

        if (token != null && token.startsWith("Bearer ")) {

            try {
                String claims = token.replace("Bearer ", "");

                Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(claims);

                Long userId = Long.parseLong(claimsJws.getBody().getSubject());
                UserAuthShortDto user = userDao.getAuthShortDtoByUserId(userId);

                if (user.getRoles() == null || user.getRoles().isEmpty()) {

                    throw new ForbiddenException("Access denied");
                }

                return user;

            } catch (Throwable t) {

                throw new ForbiddenException("Access denied");
            }
        }
        throw new ForbiddenException("Access denied");
    }
}
