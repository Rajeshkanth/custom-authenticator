package org.example.requiredaction;

import org.keycloak.Config;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class ExpiredPasswordUpdateActionFactory implements RequiredActionFactory {

    public static final String PROVIDER_ID = "force-update-old-password";
    private static final String DISPLAY_TEXT = "Update Expired Password";

    @Override
    public String getDisplayText() {
        return DISPLAY_TEXT;
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        return new ExpiredPasswordUpdateAction();
    }

    @Override
    public void init(Config.Scope config) {
//        No changes
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
//      No changes
    }

    @Override
    public void close() {
//      No changes
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
