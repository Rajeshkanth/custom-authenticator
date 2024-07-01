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

public class ResetCredentialAuthenticatorFactory implements AuthenticatorFactory {

    private static final String PROVIDER_ID = "reset-credential-authenticator";
    private static final String DISPLAY_TYPE = "Reset Credential Authenticator";
    private static final String HELP_TEXT = "Displays a form to enter a mobile number and sends an OTP for password reset.";

    @Override
    public String getDisplayType() {
        return DISPLAY_TYPE;
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
        return new ResetCredentialAuthenticator();
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
