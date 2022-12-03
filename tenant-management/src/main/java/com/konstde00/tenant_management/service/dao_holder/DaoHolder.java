package com.konstde00.tenant_management.service.dao_holder;

import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.Map;

public interface DaoHolder extends SmartInitializingSingleton {

    void addNewTemplates(Map<Object, Object> dataSources);
}