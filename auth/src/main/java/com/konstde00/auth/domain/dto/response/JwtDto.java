package com.konstde00.auth.domain.dto.response;

import lombok.*;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class JwtDto {

    Long userId;

    String email;

    List<String> roles;

    @ToString.Exclude
    String token;
}
