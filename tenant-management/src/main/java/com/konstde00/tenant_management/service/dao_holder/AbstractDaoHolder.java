package com.konstde00.tenant_management.service.dao_holder;

import com.konstde00.tenant_management.repository.dao.TenantDao;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@Service
@FieldDefaults(level = PROTECTED, makeFinal = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public abstract class AbstractDaoHolder implements SmartInitializingSingleton {

    @NonFinal
    Map<Long, TenantDao> templates;

    public abstract void addNewTemplates(Map<Object, Object> dataSources);
}
