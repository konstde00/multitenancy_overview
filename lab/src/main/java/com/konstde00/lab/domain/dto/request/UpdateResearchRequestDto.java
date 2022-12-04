package com.konstde00.lab.domain.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateResearchRequestDto {

    Long id;

    String name;

    String description;
}
