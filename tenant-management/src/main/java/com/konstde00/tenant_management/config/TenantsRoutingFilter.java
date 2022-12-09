package com.konstde00.tenant_management.config;

import com.konstde00.tenant_management.service.data_source.DataSourceContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Order(2)
public class TenantsRoutingFilter extends OncePerRequestFilter {

    DataSourceContextHolder dataSourceContextHolder;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        dataSourceContextHolder.updateTenantContext(request);

        filterChain.doFilter(request, response);
    }
}
