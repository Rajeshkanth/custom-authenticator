package org.example.authenticator.onboard;

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

public class OnBoardingViaMobile implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(OnBoardingViaMobile.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("Entering in onboard authenticate.");
        boolean isRememberMeEnabled = context.getRealm().isRememberMe();
        context.form().setAttribute(IS_REMEMBER_ME_ALLOWED, isRememberMeEnabled);
        context.form().setAttribute(LOGIN_FLOW, context.getAuthenticationSession().getAuthenticatedUser());
        context.challenge(context.form().createForm(REGISTER_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("Entering in onboarding action.");
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String mobileNumber = formParams.getFirst(MOBILE_NUMBER);
        String password = formParams.getFirst(PASSWORD);
        String confirmPassword = formParams.getFirst(CONFIRM_PASSWORD);

        if (isFormIncomplete(mobileNumber, password, confirmPassword)) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, REQUIRED_FIELDS, REGISTER_PAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, NO_MATCHING_PASSWORD, REGISTER_PAGE);
            return;
        }

        if (findUser(context.getSession(), context.getRealm(), mobileNumber) != null) {
            showError(context, AuthenticationFlowError.GENERIC_AUTHENTICATION_ERROR, USER_EXISTS, REGISTER_PAGE);
            return;
        }

        try {
            logger.info("Executing onboard success.");
            storeTemporaryUserData(context, mobileNumber, password);
            context.success();
        }catch (Exception e){
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INTERNAL_ERROR, INTERNAL_ERROR, REGISTER_PAGE);
        }

    }

    private boolean isFormIncomplete(String mobileNumber, String password, String confirmPassword) {
        return mobileNumber == null || mobileNumber.isEmpty() ||
                password == null || password.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty();
    }

    private void storeTemporaryUserData(AuthenticationFlowContext context, String mobileNumber, String password) {
        context.getAuthenticationSession().setAuthNote(TEMP_USER_NAME, mobileNumber);
        context.getAuthenticationSession().setAuthNote(TEMP_PASSWORD, password);
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
        // No required actions
    }

    @Override
    public void close() {
        // No resources to close
    }
}
