package org.example.scheduler.rest;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerResourceProviderFactory implements RealmResourceProviderFactory {
    public static final String PROVIDER_ID = "scheduler";
    public static final Logger logger = LoggerFactory.getLogger(SchedulerResourceProviderFactory.class);
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        logger.info("Running in SchedulerResourceProviderFactory create method");
        return new SchedulerResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
