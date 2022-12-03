package com.konstde00.auth.domain.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTenantRequestDto {

    String name;

    String dbName;

    String userName;

    String dbPassword;

    CreateUserRequestDto user;
}
