package com.konstde00.lab.domain.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateResearchRequestDto {

    String name;

    String description;
}
