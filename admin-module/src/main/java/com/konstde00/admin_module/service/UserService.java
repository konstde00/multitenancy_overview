package com.konstde00.admin_module.service;

import com.konstde00.admin_module.domain.dto.request.CreateUserRequestDto;
import com.konstde00.admin_module.mapper.UserMapper;
import com.konstde00.admin_module.repository.UserRepository;
import com.konstde00.commons.domain.entity.Tenant;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.domain.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    public void createUser(CreateUserRequestDto requestDto, Tenant tenant) {

        User user = UserMapper.INSTANCE.fromRequestDto(requestDto);

        user.setRole(Role.USER);
        user.setTenant(tenant);

        userRepository.saveAndFlush(user);
    }
}
