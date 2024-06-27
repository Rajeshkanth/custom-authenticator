package org.example.authenticator.password;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.requiredaction.ExpiredPasswordUpdateAction;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDate;

import static org.example.authenticator.utils.Constants.UPDATE_PASSWORD_PAGE;

public class UpdatePasswordAuthenticator implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(UpdatePasswordAuthenticator.class);
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        context.challenge(context.form().createForm("update-password.ftl"));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String password = formParams.getFirst("password");
        String confirmPassword = formParams.getFirst("confirmPassword");
        UserModel user = context.getUser();

        if (!password.equals(confirmPassword)) {
            context.challenge(context.form().setError("Password doesn't matches with confirm password!").createForm(UPDATE_PASSWORD_PAGE));
            return;
        }

        user.credentialManager().updateCredential(UserCredentialModel.password(password, false));
        user.setSingleAttribute("passwordLastChanged", LocalDate.now().toString());
        context.success();
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
