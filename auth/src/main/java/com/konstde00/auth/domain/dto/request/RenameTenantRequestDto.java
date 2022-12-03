package com.konstde00.auth.domain.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RenameTenantRequestDto {

    Long id;

    String name;
}
