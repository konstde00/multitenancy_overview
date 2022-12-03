package com.konstde00.tenant_management.service;

import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.domain.enums.Role;
import com.konstde00.commons.exceptions.ResourceNotFoundException;
import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import com.konstde00.tenant_management.mapper.UserMapper;
import com.konstde00.tenant_management.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

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

    public void createUser(CreateUserRequestDto requestDto, Tenant tenant) {

        User user = UserMapper.INSTANCE.fromRequestDto(requestDto);

        user.setRoles(List.of(Role.ADMIN));
        user.setTenant(tenant);

        userRepository.saveAndFlush(user);
    }
}
