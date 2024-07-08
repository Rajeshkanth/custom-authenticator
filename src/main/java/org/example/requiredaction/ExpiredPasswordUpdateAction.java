package org.example.requiredaction;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyManagerProvider;
import org.keycloak.policy.PolicyError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.example.authenticator.utils.Constants.*;

public class ExpiredPasswordUpdateAction implements RequiredActionProvider {

    private static final Logger logger = LoggerFactory.getLogger(ExpiredPasswordUpdateAction.class);

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        logger.info("Entered in expired password update trigger.");
        UserModel user = context.getUser();
        String passwordExpireDate = user.getFirstAttribute(PASSWORD_LAST_CHANGED);

        if (passwordExpireDate == null) {
            logger.error("Password expire date is missing or null!");
            return;
        }

        if (!isPasswordExpired(passwordExpireDate)) {
            logger.info("Password is not expired yet!");
            return;
        }

        user.addRequiredAction(ExpiredPasswordUpdateActionFactory.PROVIDER_ID);
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        logger.info("Entered in expired password update required action challenge.");
        context.challenge(context.form().setError(UPDATE_EXPIRED_PASSWORD).createForm(UPDATE_PASSWORD_PAGE));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        logger.info("Entered in expired password update process action.");
        String password = context.getHttpRequest().getDecodedFormParameters().getFirst(PASSWORD);
        String confirmPassword = context.getHttpRequest().getDecodedFormParameters().getFirst(CONFIRM_PASSWORD);
        UserModel user = context.getUser();

        if (password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            logger.error(PASSWORD_REQUIRED);
            context.challenge(context.form().setError(PASSWORD_REQUIRED).createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        if (!password.equals(confirmPassword)) {
            logger.error(NO_MATCHING_PASSWORD);
            context.challenge(context.form().setError(NO_MATCHING_PASSWORD).createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        String passwordError = isPasswordMatchesPasswordPolicies(context, context.getUser().getUsername(), password);
        if (passwordError != null) {
            context.challenge(context.form().setError(passwordError).createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        user.credentialManager().updateCredential(UserCredentialModel.password(confirmPassword));
        user.setSingleAttribute(PASSWORD_LAST_CHANGED, LocalDate.now().toString());
        user.removeRequiredAction(ExpiredPasswordUpdateActionFactory.PROVIDER_ID);
        context.success();
    }

    private String isPasswordMatchesPasswordPolicies(RequiredActionContext context, String mobileNumber, String password) {
        logger.info("Validating password with policy");
        PasswordPolicyManagerProvider passwordPolicy = context.getSession().getProvider(PasswordPolicyManagerProvider.class);
        PolicyError passwordError = passwordPolicy.validate(mobileNumber, password);

        return passwordError != null ? passwordError.getMessage() : null;
    }

    public boolean isPasswordExpired(String passwordExpireDate) {
        logger.info("Checking is password expired...");
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = LocalDate.parse(passwordExpireDate).plusDays(PASSWORD_EXPIRY_DAYS);
        return currentDate.isAfter(expiryDate);
    }

    @Override
    public void close() {
//      No need
    }
}
