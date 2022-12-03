package com.konstde00.tenant_management.controller;

import com.konstde00.commons.domain.enums.Role;
import com.konstde00.tenant_management.domain.dto.request.CreateTenantRequestDto;
import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import com.konstde00.tenant_management.domain.dto.response.CreateUserResponseDto;
import com.konstde00.tenant_management.domain.dto.response.TenantResponseDto;
import com.konstde00.tenant_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/v1")
    @Operation(summary = "Create a new tenant")
    public ResponseEntity<CreateUserResponseDto> createTenant(@RequestBody CreateUserRequestDto userDto,
                                                              HttpServletRequest request) {

        CreateUserResponseDto user = userService.create(userDto, List.of(Role.USER), request);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
