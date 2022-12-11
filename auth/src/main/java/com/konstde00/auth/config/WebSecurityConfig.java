package com.konstde00.auth.config;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.konstde00.commons.domain.enums.Role.*;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .addFilter(new JwtAuthenticationFilter(authenticationManagerBean(), jwtSecret))
                .exceptionHandling()
                .authenticationEntryPoint((request, response, exception) ->
                        response.sendError(SC_FORBIDDEN, "You're not authorized to perform such action."))
                .and()
                .authorizeRequests()
                .antMatchers("/v3/docs/**", "/swagger-ui/**", "/docs/**").permitAll()
                .antMatchers("/api/login/**").permitAll()
                .antMatchers("/api/tenants/**").hasAuthority(SYS_ADMIN.getValue())
                .antMatchers(HttpMethod.GET, "/api/researches/**").hasAnyAuthority(SYS_ADMIN.getValue(), ADMIN.getValue(), USER.getValue())
                .antMatchers(HttpMethod.POST, "/api/researches/**").hasAnyAuthority(SYS_ADMIN.getValue(), ADMIN.getValue())
                .antMatchers(HttpMethod.PATCH, "/api/researches/**").hasAnyAuthority(SYS_ADMIN.getValue(), ADMIN.getValue())
                .antMatchers(HttpMethod.DELETE, "/api/researches/**").hasAnyAuthority(SYS_ADMIN.getValue(), ADMIN.getValue())
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .csrf().disable().logout().disable();
    }
}
