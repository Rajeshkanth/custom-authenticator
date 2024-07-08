package org.example.policy;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

import static org.example.authenticator.utils.Constants.*;

public class NoBrithDatePasswordPolicyProviderFactory implements PasswordPolicyProviderFactory {

    private static final String DISPLAY_NAME = "No birth date allowed in password";
    private static final String PROVIDER_ID = "noDateOfBirthAllowed";
    private static final String CONFIG_PROPERTY_NAME = "Check Type";
    private static final String CONFIG_PROPERTY_LABEL = "Select which parts of DOB to check in the password";
    private static final String CONFIG_PROPERTY_HELP = "Select 'fullDob' to check the full date of birth, 'day', 'month', or 'year'.";

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getConfigType() {
        return ProviderConfigProperty.LIST_TYPE;
    }

    @Override
    public String getDefaultConfigValue() {
        return FULL_DOB;
    }

    @Override
    public boolean isMultiplSupported() {
        return false;
    }

    @Override
    public PasswordPolicyProvider create(KeycloakSession session) {
        return new NoBirthDatePasswordPolicyProvider(session.getContext());
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

    public List<ProviderConfigProperty> getConfigProperties(){
            List<ProviderConfigProperty> configProperties = new ArrayList<>();
            ProviderConfigProperty configProperty = new ProviderConfigProperty();
            configProperty.setName(CONFIG_PROPERTY_NAME);
            configProperty.setType(ProviderConfigProperty.LIST_TYPE);
            configProperty.setLabel(CONFIG_PROPERTY_LABEL);
            configProperty.setHelpText(CONFIG_PROPERTY_HELP);

            List<String> options = new ArrayList<>();
            options.add(FULL_DOB);
            options.add(DATE);
            options.add(MONTH);
            options.add(YEAR);

            configProperty.setOptions(options);
            configProperties.add(configProperty);

            return configProperties;
    }
}
