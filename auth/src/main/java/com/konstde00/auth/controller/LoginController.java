package com.konstde00.auth.controller;

import com.konstde00.auth.domain.dto.request.LoginByUsernameDto;
import com.konstde00.auth.domain.dto.response.JwtDto;
import com.konstde00.auth.service.LoginService;
import com.konstde00.auth.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/login")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginController {

    LoginService loginService;

    @PostMapping("/v1/email")
    @Operation(summary = "Login with email")
    public JwtDto login(@RequestBody LoginByUsernameDto authenticationInfo) {

        return loginService.authorizationByUsername(authenticationInfo);
    }
}
