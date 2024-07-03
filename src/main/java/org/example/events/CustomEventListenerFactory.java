package org.example.events;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class CustomEventListenerFactory implements EventListenerProviderFactory {

    public static final String PROVIDER_ID = "custom-event-listener";
    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new CustomEventListener(session);
    }

    @Override
    public void init(Config.Scope config) {
//      No action needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
//      No action needed
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
