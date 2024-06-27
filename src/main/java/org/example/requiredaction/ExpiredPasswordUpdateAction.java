package org.example.requiredaction;

import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.example.authenticator.utils.Constants.PASSWORD_EXPIRY_DAYS;
import static org.example.authenticator.utils.Constants.UPDATE_PASSWORD_PAGE;

public class ExpiredPasswordUpdateAction implements RequiredActionProvider {

    private static final Logger logger = LoggerFactory.getLogger(ExpiredPasswordUpdateAction.class);

    @Override
    public void evaluateTriggers(RequiredActionContext context) {
        UserModel user = context.getUser();
        String pswdExpDate = user.getFirstAttribute("passwordLastChanged");

        if (pswdExpDate == null) {
            logger.error("Password expire date is missing or null!");
            return;
        }

        if (!isPasswordExpired(pswdExpDate)) {
            logger.info("Password is not expired yet!");
            return;
        }

        user.addRequiredAction(ExpiredPasswordUpdateActionFactory.PROVIDER_ID);
    }

    @Override
    public void requiredActionChallenge(RequiredActionContext context) {
        context.challenge(context.form().createForm(UPDATE_PASSWORD_PAGE));
    }

    @Override
    public void processAction(RequiredActionContext context) {
        String password = context.getHttpRequest().getDecodedFormParameters().getFirst("password");
        String confirmPassword = context.getHttpRequest().getDecodedFormParameters().getFirst("confirmPassword");
        UserModel user = context.getUser();

        if (password == null || password.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            context.challenge(context.form().setError("Password required").createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        if (!password.equals(confirmPassword)) {
            context.challenge(context.form().setError("Password doesn't matches with confirm password!").createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        user.credentialManager().updateCredential(UserCredentialModel.password(confirmPassword));
        user.setSingleAttribute("passwordLastChanged", LocalDate.now().toString());
        user.removeRequiredAction(ExpiredPasswordUpdateActionFactory.PROVIDER_ID);
        context.success();
    }

    public boolean isPasswordExpired(String pswdExpDate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = LocalDate.parse(pswdExpDate).plusDays(PASSWORD_EXPIRY_DAYS);
        return currentDate.isAfter(expiryDate);
    }

    @Override
    public void close() {
//      No need
    }
}
