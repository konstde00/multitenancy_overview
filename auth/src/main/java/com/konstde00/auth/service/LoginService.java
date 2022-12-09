package com.konstde00.auth.service;

import com.konstde00.auth.domain.dto.request.LoginByUsernameDto;
import com.konstde00.auth.domain.dto.response.JwtDto;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.exceptions.NotValidException;
import com.konstde00.tenant_management.service.UserService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@AllArgsConstructor
@DependsOn("dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginService {

    UserService userService;
    TokenService tokenService;

    public JwtDto authorizationByUsername(LoginByUsernameDto authenticationInfo) {

        User user = userService.getByEmail(authenticationInfo.getEmail());

        validatePassword(authenticationInfo.getPassword(), user.getPassword());

        return tokenService.generate(user);
    }

    private void validatePassword(String inputPassword, String correctPassword) {

        boolean passwordValidation = BCrypt.checkpw(inputPassword, correctPassword);

        if (!passwordValidation) {

            log.error("Password is incorrect.");

            throw new NotValidException("Password is incorrect.");
        }
    }
}
