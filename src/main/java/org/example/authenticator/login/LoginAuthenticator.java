package org.example.authenticator.login;

import jakarta.ws.rs.core.MultivaluedMap;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.Errors;
import org.keycloak.models.*;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.PasswordCredentialProvider;
import org.keycloak.services.managers.BruteForceProtector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;
import static org.example.authenticator.utils.FindUser.findUser;

public class LoginAuthenticator implements Authenticator {
    private static final Logger logger = LoggerFactory.getLogger(LoginAuthenticator.class);

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
        RealmModel realm = context.getRealm();
        BruteForceProtector protector = context.getSession().getProvider(BruteForceProtector.class);

        if (user == null) {
            logger.info(USER_NOT_FOUND);
            showError(context, AuthenticationFlowError.INVALID_USER, USER_NOT_FOUND, LOGIN_PAGE);
            return;
        }

        if (protector.isTemporarilyDisabled(context.getSession(), realm, user)) {
            logger.warn("User is temporarily disabled.");
            context.getEvent().user(user);
            context.getEvent().error(Errors.USER_TEMPORARILY_DISABLED);
            showError(context, AuthenticationFlowError.INVALID_USER, USER_TEMPORARILY_DISABLED, LOGIN_PAGE);
            return;
        }

        if (validatePassword(context.getSession(), realm, user, UserCredentialModel.password(password))) {
            protector.successfulLogin(realm, user, context.getConnection());
            authenticateUser(context, user);
        } else {
            logger.info("Disabling user");
            context.getEvent().user(user);
            context.getEvent().error(Errors.INVALID_USER_CREDENTIALS);
            protector.failedLogin(realm, user, context.getConnection());
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_CREDENTIALS, LOGIN_PAGE);
        }
    }

    private void authenticateUser(AuthenticationFlowContext context, UserModel user) {
        context.getAuthenticationSession().setAuthenticatedUser(user);
        user.setSingleAttribute(LAST_LOGIN, LocalDate.now().toString());
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
