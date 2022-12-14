package com.konstde00.tenant_management.domain.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RenameTenantRequestDto {

    Long id;

    String name;
}
