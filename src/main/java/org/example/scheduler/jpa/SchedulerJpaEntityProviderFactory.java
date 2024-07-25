package org.example.scheduler.jpa;

import org.keycloak.Config;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerJpaEntityProviderFactory implements JpaEntityProviderFactory {
    public static final String PROVIDER_ID = "scheduler-entity-provider";
    private static final Logger logger = LoggerFactory.getLogger(SchedulerJpaEntityProviderFactory.class);

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        logger.info("In SchedulerJpaEntityProviderFactory create method");
        return new SchedulerJpaEntityProvider();
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
        return PROVIDER_ID;
    }
}
