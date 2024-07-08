package org.example.policy;

import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.example.authenticator.utils.Constants.*;

public class NoRepeatedCharactersPasswordPolicyProvider implements PasswordPolicyProvider {

    private static final Logger logger = LoggerFactory.getLogger(NoRepeatedCharactersPasswordPolicyProvider.class);
    public final KeycloakContext context;

    public NoRepeatedCharactersPasswordPolicyProvider(KeycloakContext context) {
        this.context = context;
    }

    @Override
    public PolicyError validate(RealmModel realm, UserModel user, String password) {
        logger.info("Validating repetitive characters present in password.");
        Integer repetitiveChars = context.getRealm().getPasswordPolicy().getPolicyConfig(NO_REPETITIVE_PASSWORD_POLICY_PROVIDER_ID);

        if (isRepetitiveCharactersPresent(password)) {
            logger.error("Repeated characters found.");
            return new PolicyError(REPEATED_CHARACTERS_PRESENT_IN_PASSWORD, repetitiveChars);
        }
        return null;
    }

    @Override
    public PolicyError validate(String user, String password) {
        Integer repetitiveChars = context.getRealm().getPasswordPolicy().getPolicyConfig(NO_REPETITIVE_PASSWORD_POLICY_PROVIDER_ID);

        if (isRepetitiveCharactersPresent(password)) {
            logger.error("Repeated characters found.");
            return new PolicyError(REPEATED_CHARACTERS_PRESENT_IN_PASSWORD, repetitiveChars);
        }
        return null;
    }

    @Override
    public Object parseConfig(String value) {
        return this.parseInteger(value, 1);
    }

    public boolean isRepetitiveCharactersPresent(String password) {
        Set<Character> seenCharacters = new HashSet<>();

        for (char c : password.toCharArray()) {
            if (seenCharacters.contains(c)) {
                logger.error("Repetitive characters found in entered password.");
                return true;
            }
            seenCharacters.add(c);
        }
        return false;
    }

    @Override
    public void close() {
//       Not needed
    }
}
