package com.konstde00.auth.config;

import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Order(1)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    String jwtSecret;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret) {
        super(authenticationManager);
        this.jwtSecret = jwtSecret;
    }

    static String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        var authentication = parseToken(request);

        if (authentication != null)
            SecurityContextHolder.getContext().setAuthentication(authentication);
        else
            SecurityContextHolder.clearContext();

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken parseToken(HttpServletRequest request) {

        var token = request.getHeader(AUTHORIZATION);

        if (token != null && token.startsWith(BEARER)) {

            try {
                val claims = token.replace(BEARER, StringUtils.EMPTY);

                val claimsJws = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(claims);

                val userId = Long.parseLong(claimsJws.getBody().getSubject());

                val roles = ((Collection<String>) claimsJws.getBody()
                        .get("roles"))
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                return new UsernamePasswordAuthenticationToken(userId, new Object(), roles);

            } catch (Exception e) {

                log.error("Token is not valid");
            }
        }

        return null;
    }
}
