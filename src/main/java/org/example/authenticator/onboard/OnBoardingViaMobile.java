package org.example.authenticator.onboard;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.Details;
import org.keycloak.events.EventBuilder;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
            if (isSmsFlowRequired(context)) {
                logger.info("SMS flow required.");
                storeTemporaryUserData(context, mobileNumber, password);
                context.success();
            } else {
                logger.info("SMS flow disabled.");
                createUserAndAuthenticate(context, mobileNumber, password);
            }
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INTERNAL_ERROR, INTERNAL_ERROR, REGISTER_PAGE);
        }
    }

    private void createUserAndAuthenticate(AuthenticationFlowContext context, String mobileNumber, String password) {
        try {
            logger.info("Creating user without sms validation.");
            UserModel newUser = context.getSession().users().addUser(context.getRealm(), mobileNumber);
            newUser.setEnabled(true);
            newUser.credentialManager().updateCredential(UserCredentialModel.password(password, false));
            newUser.setSingleAttribute(PASSWORD_LAST_CHANGED, LocalDate.now().toString());
            context.getAuthenticationSession().setAuthenticatedUser(newUser);

            triggerRegisterEvent(context, newUser);
            context.success();
            logger.info("User {} created successfully", mobileNumber);
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INTERNAL_ERROR, VERIFY_OTP_PAGE);
        }
    }

    private boolean isFormIncomplete(String mobileNumber, String password, String confirmPassword) {
        return mobileNumber == null || mobileNumber.isEmpty() ||
                password == null || password.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty();
    }

    private boolean isSmsFlowRequired(AuthenticationFlowContext context) {
        String currentExecutionId = context.getExecution().getId();
        AuthenticationExecutionModel currentExecution = context.getRealm().getAuthenticationExecutionById(currentExecutionId);
        String parentFlowId = currentExecution.getParentFlow();
        List<AuthenticationExecutionModel> executions = context.getRealm().getAuthenticationExecutionsStream(parentFlowId).collect(Collectors.toList());

        for (AuthenticationExecutionModel execution : executions) {
            if (SMS_PROVIDER_ID.equals(execution.getAuthenticator()) && execution.getRequirement() == AuthenticationExecutionModel.Requirement.REQUIRED) {
                return true;
            }
        }
        return false;
    }

    public void triggerRegisterEvent(AuthenticationFlowContext context, UserModel newUser){
        logger.info("Triggering Register Event.");
        EventBuilder eventBuilder = context.getEvent()
                .clone().event(EventType.REGISTER)
                .client(context.getAuthenticationSession().getClient())
                .user(newUser);

        eventBuilder.detail(Details.USERNAME, newUser.getUsername())
                .detail(Details.REGISTER_METHOD, REGISTER_PROVIDER_ID)
                .success();
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
