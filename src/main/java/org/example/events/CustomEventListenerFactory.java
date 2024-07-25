package org.example.events;

import org.example.scheduler.RemoveInactiveUserTask;
import org.example.scheduler.SchedulerProvider;
import org.example.scheduler.jpa.SchedulerProviderModel;
import org.example.scheduler.jpa.SchedulerProviderRepresentation;
import org.example.scheduler.service.impl.SchedulerServiceImpl;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.utils.PostMigrationEvent;
import org.keycloak.timer.TimerProvider;
import org.keycloak.timer.TimerProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;


public class CustomEventListenerFactory implements EventListenerProviderFactory {

    public static final String PROVIDER_ID = "custom-event-listener";
    private static final Logger logger = LoggerFactory.getLogger(CustomEventListenerFactory.class);

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        logger.info("In create method in CustomEventListener, {}", session.getContext().getRealm());
        return new CustomEventListener(session);
    }

    @Override
    public void init(Config.Scope config) {
        // do nothing
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        factory.register(event -> {
            if (event instanceof PostMigrationEvent) {
                try (KeycloakSession session = factory.create()) {
                    SchedulerServiceImpl schedulerService = new SchedulerServiceImpl(session);
                    List<SchedulerProviderModel> list = schedulerService.getSchedulerProviders();
                    for (SchedulerProviderModel scheduler : list) {
                        if (scheduler.isEnabled()) {
                            SchedulerProvider provider = session.getProvider(SchedulerProvider.class, scheduler.getProviderId());
                            if (provider != null) {
                                provider.restoreTasks();
                            } else {
                                logger.warn("No provider found for ID: " + scheduler.getProviderId());
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    public void close() {
//      No action needed
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
