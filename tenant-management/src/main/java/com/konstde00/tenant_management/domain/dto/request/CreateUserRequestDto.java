package com.konstde00.tenant_management.domain.dto.request;

import com.konstde00.commons.domain.enums.Role;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequestDto {

    Long tenantId;

    String email;

    String password;

    List<Role> roles;
}
