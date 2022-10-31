package com.konstde00.admin_module.service.dao_holder;

import org.springframework.beans.factory.SmartInitializingSingleton;

import java.util.Map;

public interface DaoHolder extends SmartInitializingSingleton {

    void addNewTemplates(Map<Object, Object> dataSources);
}
