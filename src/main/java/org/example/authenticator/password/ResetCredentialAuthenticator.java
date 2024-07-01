package org.example.authenticator.password;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;
import static org.example.authenticator.utils.FindUser.findUser;


// It is responsible for presenting the mobile number form page for getting otp to validate the number
// and after successful auth update password form page will get presented.
public class ResetCredentialAuthenticator implements Authenticator {
    private static final Logger logger = LoggerFactory.getLogger(ResetCredentialAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("Entered in Reset credential authenticate.");
        context.challenge(context.form().createForm(FORGOT_PASSWORD_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("Entered in Reset Credential action.");
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formParams.getFirst(MOBILE_NUMBER);
        UserModel existingUser = findUser(context.getSession(), context.getRealm(), mobileNumber);

        if (existingUser != null) {
            context.getAuthenticationSession().setAuthenticatedUser(existingUser);
            context.success();
        } else {
            showError(context, AuthenticationFlowError.INVALID_USER, USER_NOT_FOUND, FORGOT_PASSWORD_PAGE);
        }
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
        // Not needed
    }

    @Override
    public void close() {
        // Not needed
    }
}
