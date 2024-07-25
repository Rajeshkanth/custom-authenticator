package org.example.scheduler;

import org.keycloak.Config;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class RemoveInactiveUserProviderFactory implements SchedulerProviderFactory, ConfigurableAuthenticatorFactory {
    private static final String PROVIDER_ID = "basic-scheduler";
    private static final String DISPLAY_TEXT = "REMOVE INACTIVE USER";
    private static final String HELP_TEXT = "It will remove all the users who are all inactive for specified period.";
    private static final String INTERVAL_CONFIG_KEY = "interval";

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }
    
    @Override
    public SchedulerProvider create(KeycloakSession session) {
        return new RemoveInactiveUser(session);
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

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> configProperties = new ArrayList<>();

        ProviderConfigProperty interval = new ProviderConfigProperty();
        interval.setName(INTERVAL_CONFIG_KEY);
        interval.setLabel("Interval Time");
        interval.setType(ProviderConfigProperty.STRING_TYPE);
        interval.setHelpText("Interval time in minutes to run the task.");

        configProperties.add(interval);

        return configProperties;
    }

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
        return new AuthenticationExecutionModel.Requirement[0];
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }
}
