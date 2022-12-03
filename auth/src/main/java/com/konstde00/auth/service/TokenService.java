package com.konstde00.auth.service;

import com.konstde00.auth.domain.dto.response.JwtDto;
import com.konstde00.commons.domain.entity.User;
import com.konstde00.commons.domain.enums.Role;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.stream.Collectors;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
//@DependsOn("dataSourceRouting")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@PropertySource(value = "classpath:application.yml")
public class TokenService {

    @NonFinal
    @Value("${jwt.secret}")
    String jwtSecret;

    @NonFinal
    @Value("${token.expiration.time.sec:1440}")
    Long expirationTimeSec;

    public JwtDto generate(User user) {

        SecretKey secretKey = hmacShaKeyFor(jwtSecret.getBytes());

        Date expired = Date.from(LocalDateTime.now().plusSeconds(expirationTimeSec).toInstant(ZoneOffset.UTC));

        return JwtDto.builder()
                .userId(user.getId())
                .token(toToken(user, expired, secretKey))
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::toString).collect(Collectors.toList()))
                .build();
    }

    private static String toToken(User user, Date expired, SecretKey secretKey) {
        return Jwts.builder()
                .signWith(HS512, secretKey)
                .setSubject(Long.toString(user.getId()))
                .setExpiration(expired)
                .claim("userId", user.getId())
                .claim("roles", user.getRoles().stream().map(Role::toString).collect(Collectors.toList()))
                .claim("email", user.getEmail())
                .compact();
    }
}
