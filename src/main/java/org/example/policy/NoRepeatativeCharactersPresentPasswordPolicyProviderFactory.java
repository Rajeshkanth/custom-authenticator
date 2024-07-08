package org.example.policy;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;

import static org.example.authenticator.utils.Constants.*;

public class NoRepeatativeCharactersPresentPasswordPolicyProviderFactory implements PasswordPolicyProviderFactory {
    private static final String DISPLAY_TEXT = "No repeated characters allowed";
    public static final String CONFIG_TYPE = "int";
    public static final String DEFAULT_CONFIG_VALUE = "2";

    @Override
    public String getDisplayName() {
        return DISPLAY_TEXT;
    }

    @Override
    public String getConfigType() {
        return CONFIG_TYPE;
    }

    @Override
    public String getDefaultConfigValue() {
        return DEFAULT_CONFIG_VALUE;
    }

    @Override
    public boolean isMultiplSupported() {
        return false;
    }

    @Override
    public PasswordPolicyProvider create(KeycloakSession session) {
        return new NoRepeatedCharactersPasswordPolicyProvider(session.getContext());
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
        return NO_REPETITIVE_PASSWORD_POLICY_PROVIDER_ID;
    }
}
