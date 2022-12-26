package com.konstde00.tenant_management.domain.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTenantRequestDto {

    @NotBlank
    String name;

    @NotBlank
    String dbName;

    @NotBlank
    String userName;

    @NotBlank
    String dbPassword;

    @NotBlank
    CreateUserRequestDto user;
}
