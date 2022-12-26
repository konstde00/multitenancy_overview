package com.konstde00.tenant_management.domain.dto.response;

import com.konstde00.commons.domain.enums.DatabaseCreationStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantResponseDto {

    Long id;

    Long userId;

    String name;

    String dbName;

    DatabaseCreationStatus creationStatus;
}
