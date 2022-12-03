package com.konstde00.tenant_management.domain.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserResponseDto {

    Long id;

    String email;

    String password;
}
