package com.konstde00.tenant_management.service.data_source;

import com.konstde00.tenant_management.domain.dto.response.UserAuthShortDto;
import com.konstde00.tenant_management.service.UserService;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataSourceContextHolder {

    UserService userService;

    @NonFinal
    static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public DataSourceContextHolder(@Lazy UserService userService) {

        this.userService = userService;
    }

    public static void setBranchContext(Long tenantId) {

        threadLocal.set(tenantId);
    }

    public static Long getBranchContext() {

        return threadLocal.get();
    }

    public void updateContextForCompanyFromToken(HttpServletRequest request) {

        UserAuthShortDto user = userService.getActualUser(request);

        Long tenantId = user.getTenantId();

        setBranchContext(tenantId);
    }
}
