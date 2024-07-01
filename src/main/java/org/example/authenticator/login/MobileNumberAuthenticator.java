package org.example.authenticator.login;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.PasswordCredentialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;
import static org.example.authenticator.utils.FindUser.findUser;

public class MobileNumberAuthenticator implements Authenticator {
    private static final Logger logger = LoggerFactory.getLogger(MobileNumberAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("Entered in login authenticate");
        boolean isRememberMeAllowed = context.getRealm().isRememberMe();
        context.form().setAttribute(IS_REMEMBER_ME_ALLOWED, isRememberMeAllowed);
        context.form().setAttribute(LOGIN_FLOW, context.getAuthenticationSession().getAuthenticatedUser());
        context.challenge(context.form().createForm(LOGIN_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("Entered in login action");
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formParams.getFirst(MOBILE_NUMBER);
        String password = formParams.getFirst(PASSWORD);

        if (isValidInput(mobileNumber, password)) {
            handleLoginForm(context, mobileNumber, password);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, REQUIRED_FIELDS, LOGIN_PAGE);
        }
    }

    private boolean isValidInput(String mobileNumber, String password) {
        return mobileNumber != null && password != null && !mobileNumber.isEmpty() && !password.isEmpty();
    }

    private void handleLoginForm(AuthenticationFlowContext context, String mobileNumber, String password) {
        UserModel user = findUser(context.getSession(), context.getRealm(), mobileNumber);
        if (user != null && validatePassword(context.getSession(), context.getRealm(), user, UserCredentialModel.password(password))) {
            authenticateUser(context, user);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_CREDENTIALS, LOGIN_PAGE);
        }
    }

    private void authenticateUser(AuthenticationFlowContext context, UserModel user) {
        context.getAuthenticationSession().setAuthenticatedUser(user);
        context.success();
    }

    private boolean validatePassword(KeycloakSession session, RealmModel realm, UserModel user, CredentialInput password) {
        PasswordCredentialProvider passwordCredentialProvider = new PasswordCredentialProvider(session);
        return passwordCredentialProvider.isValid(realm, user, password);
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return false;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions to set
    }

    @Override
    public void close() {
        // No resources to close
    }
}
