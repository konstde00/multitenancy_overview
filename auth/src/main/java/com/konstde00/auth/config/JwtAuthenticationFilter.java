package com.konstde00.auth.config;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
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
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private final String jwtSecret;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret) {
        super(authenticationManager);
        this.jwtSecret = jwtSecret;
    }

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

        if (token != null && token.startsWith("Bearer ")) {

            try {
                var claims = token.replace("Bearer ", "");

                var claimsJws = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(claims);

                var userId = Long.parseLong(claimsJws.getBody().getSubject());

                var roles = ((Collection<String>) claimsJws.getBody()
                        .get("roles"))
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                return new UsernamePasswordAuthenticationToken(userId, null, roles);

            } catch (Exception e) {

                log.error("Token is not valid");
            }
        }

        return null;
    }
}
