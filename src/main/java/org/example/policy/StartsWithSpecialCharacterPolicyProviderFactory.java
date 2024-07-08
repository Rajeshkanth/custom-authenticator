package org.example.policy;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;

import static org.example.authenticator.utils.Constants.PASSWORD_STARTS_WITH_SPECIAL_CHAR_PROVIDER_ID;

public class StartsWithSpecialCharacterPolicyProviderFactory implements PasswordPolicyProviderFactory {

    private static final String DISPLAY_NAME = "Starts with special character";
    @Override
    public PasswordPolicyProvider create(KeycloakSession session) {
        return new StartsWithSpecialCharacterPolicyProvider(session.getContext());
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getConfigType() {
        return null;
    }

    @Override
    public String getDefaultConfigValue() {
        return null;
    }

    @Override
    public boolean isMultiplSupported() {
        return false;
    }

    @Override
    public void init(Config.Scope config) {
//      Not needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
//      Not needed
    }

    @Override
    public void close() {
//      Not needed
    }

    @Override
    public String getId() {
        return PASSWORD_STARTS_WITH_SPECIAL_CHAR_PROVIDER_ID;
    }
}
