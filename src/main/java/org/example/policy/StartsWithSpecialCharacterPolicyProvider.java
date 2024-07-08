package org.example.policy;

import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.authenticator.utils.Constants.*;

// This provider is used for creating a password with special character
public class StartsWithSpecialCharacterPolicyProvider implements PasswordPolicyProvider {

    private static final Logger logger = LoggerFactory.getLogger(StartsWithSpecialCharacterPolicyProvider.class);
    private final KeycloakContext context;

    public StartsWithSpecialCharacterPolicyProvider(KeycloakContext context) {
        this.context = context;
    }

    @Override
    public PolicyError validate(RealmModel realm, UserModel user, String password) {
        logger.info("Validating in Starts with special character in password.");
        String startsWith = context.getRealm().getPasswordPolicy().getPolicyConfig(PASSWORD_STARTS_WITH_SPECIAL_CHAR_PROVIDER_ID);
        if (Character.isLetterOrDigit(password.charAt(0))) {
            return new PolicyError(PASSWORD_MUST_STARTS_WITH_SPECIAL_CHAR, startsWith);
        }
        return null;
    }

    @Override
    public PolicyError validate(String user, String password) {
        String startsWith = context.getRealm().getPasswordPolicy().getPolicyConfig(PASSWORD_STARTS_WITH_SPECIAL_CHAR_PROVIDER_ID);
        if (Character.isLetterOrDigit(password.charAt(0))) {
            return new PolicyError(PASSWORD_MUST_STARTS_WITH_SPECIAL_CHAR, startsWith);
        }
        return null;
    }

    @Override
    public Object parseConfig(String value) {
        return null;
    }

    @Override
    public void close() {
//      not needed
    }
}
