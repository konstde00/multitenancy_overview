package com.konstde00.tenant_management.controller;

import com.konstde00.tenant_management.domain.dto.request.CreateUserRequestDto;
import com.konstde00.tenant_management.domain.dto.response.CreateUserResponseDto;
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
import static lombok.AccessLevel.PRIVATE;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserController {

    UserService userService;

    @PostMapping("/v1")
    @Operation(summary = "Create a new user")
    public ResponseEntity<CreateUserResponseDto> createUser(@RequestBody CreateUserRequestDto userDto) {

        CreateUserResponseDto user = userService.create(userDto);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }
}
