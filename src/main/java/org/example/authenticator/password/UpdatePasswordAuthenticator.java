package org.example.authenticator.password;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;

public class UpdatePasswordAuthenticator implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePasswordAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("Entered in Update password authenticate part.");
        context.challenge(context.form().createForm(UPDATE_PASSWORD_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("Entered in Update password action part.");
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String password = formParams.getFirst(PASSWORD);
        String confirmPassword = formParams.getFirst(CONFIRM_PASSWORD);
        UserModel user = context.getUser();

        if (!password.equals(confirmPassword)) {
            context.challenge(context.form().setError(NO_MATCHING_PASSWORD).createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        String passwordError = isPasswordMatchesPasswordPolicies(context, context.getUser().getUsername(), password);

        if (passwordError != null) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, passwordError, UPDATE_PASSWORD_PAGE);
            return;
        }

        user.credentialManager().updateCredential(UserCredentialModel.password(password, false));
        user.setSingleAttribute(PASSWORD_LAST_CHANGED, LocalDate.now().toString());
        context.success();
    }

    private String isPasswordMatchesPasswordPolicies(AuthenticationFlowContext context, String mobileNumber, String password) {
        logger.info("Validating password with policy");
        PasswordPolicyManagerProvider passwordPolicy = context.getSession().getProvider(PasswordPolicyManagerProvider.class);
        PolicyError passwordError = passwordPolicy.validate(mobileNumber, password);

        return passwordError != null ? passwordError.getMessage() : null;
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
//      not needed
    }

    @Override
    public void close() {
//      not needed
    }
}
