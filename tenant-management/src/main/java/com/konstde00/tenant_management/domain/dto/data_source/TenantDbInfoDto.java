package com.konstde00.tenant_management.domain.dto.data_source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class TenantDbInfoDto {

    Long key;

    String dbName;

    String userName;

    String dbPassword;
}
