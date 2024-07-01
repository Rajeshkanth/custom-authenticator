package org.example.authenticator.password;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

import java.util.List;

public class UpdatePasswordAuthenticatorFactory implements AuthenticatorFactory {
    private static final String PROVIDER_ID = "update-password-authenticator";
    private static final String DISPLAY_TEXT = "Update Password Authenticator";
    private static final String HELP_TEXT= "Displays a form to change the password after successful otp validation.";

    @Override
    public String getDisplayType() {
        return DISPLAY_TEXT;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create().build();
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new UpdatePasswordAuthenticator();
    }

    @Override
    public void init(Config.Scope config) {
        //      not needed
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        //      not needed
    }

    @Override
    public void close() {
        //      not needed
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}
