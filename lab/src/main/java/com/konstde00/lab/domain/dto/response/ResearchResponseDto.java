package com.konstde00.lab.domain.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResearchResponseDto {

    Long id;

    String name;

    String description;
}
