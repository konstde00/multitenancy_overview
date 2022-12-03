package com.konstde00.tenant_management.domain.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantResponseDto {

    Long id;

    String name;

    String dbName;

    Boolean dbCreated;
}
