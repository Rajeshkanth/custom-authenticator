package org.example.scheduler.jpa;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SchedulerJpaEntityProvider implements JpaEntityProvider {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerJpaEntityProvider.class);
    public  SchedulerJpaEntityProvider(){
        logger.info("Scheduler JPA Entity Provider running");
    }

    @Override
    public List<Class<?>> getEntities() {
        return Collections.singletonList(SchedulerProviderEntity.class);
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/services/scheduler-provider-changelog-1.0.0.xml";
    }

    @Override
    public String getFactoryId() {
        return SchedulerJpaEntityProviderFactory.PROVIDER_ID;
    }

    @Override
    public void close() {

    }
}
