package com.konstde00.auth.controller;

import com.konstde00.auth.domain.dto.request.LoginByUsernameDto;
import com.konstde00.auth.domain.dto.response.JwtDto;
import com.konstde00.auth.service.LoginService;
import com.konstde00.auth.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping("api/login")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginController {

    LoginService loginService;
    TokenService tokenService;
    //DataSourceContextHolder dataSourceContextHolder;

    static String DEFAULT_DATASOURCE_KEY = null;

    public LoginController(LoginService loginService,
                           TokenService tokenService) {
        this.loginService = loginService;
        this.tokenService = tokenService;
        //this.dataSourceContextHolder = dataSourceContextHolder;
    }

    @PostMapping("/v1/email")
    @Operation(summary = "Login with email")
    public JwtDto login(@RequestBody LoginByUsernameDto authenticationInfo) {

        String tenantKey = "";
//                dataSourceContextHolder.updateContextForCompanyFromTenantId(authenticationInfo.getTenantId());

        JwtDto dto = loginService.authorizationByUsername(authenticationInfo, tenantKey);

        //dataSourceContextHolder.setBranchContext(DEFAULT_DATASOURCE_KEY);

        return dto;
    }
}
