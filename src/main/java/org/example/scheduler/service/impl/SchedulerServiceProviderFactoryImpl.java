package org.example.scheduler.service.impl;


import org.example.scheduler.service.SchedulerService;
import org.example.scheduler.service.SchedulerServiceProviderFactory;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerServiceProviderFactoryImpl implements SchedulerServiceProviderFactory {
    public static final Logger logger = LoggerFactory.getLogger(SchedulerServiceProviderFactoryImpl.class);
    @Override
    public SchedulerService create(KeycloakSession session) {
        logger.info("Running in scheduler provider factory impl create method");
        return new SchedulerServiceImpl(session);
    }

    @Override
    public void init(Config.Scope config) {
//      do nothing
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
//      do nothing
    }

    @Override
    public void close() {
//      do nothing
    }

    @Override
    public String getId() {
        return "SchedulerServiceImpl";
    }
}
