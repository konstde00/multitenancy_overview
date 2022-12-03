package com.konstde00.commons.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@UtilityClass
public class SecurityUtils {

    public Long getTenantId(HttpServletRequest request, String jwtSecret) {

        String token = request.getHeader(AUTHORIZATION);

        if (token != null && token.startsWith("Bearer ")) {

            try {
                String claims = token.replace("Bearer ", "");

                Jws<Claims> claimsJws = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(claims);

                return claimsJws.getBody().get("tenantId", Long.class);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        throw new RuntimeException("Access denied");
    }
}
