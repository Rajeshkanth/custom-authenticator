package org.example.policy;

import org.keycloak.models.KeycloakContext;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.example.authenticator.utils.Constants.*;

public class NoBirthDatePasswordPolicyProvider implements PasswordPolicyProvider {

    private final KeycloakContext context;
    private static final Logger logger = LoggerFactory.getLogger(NoBirthDatePasswordPolicyProvider.class);

    public NoBirthDatePasswordPolicyProvider(KeycloakContext context) {
        this.context = context;
    }

    @Override
    public PolicyError validate(RealmModel realm, UserModel user, String password) {
        return this.validate(user.getUsername(), password);
    }

    @Override
    public PolicyError validate(String user, String password) {
        logger.info("Validating password contains date of birth");
        String noDobInPassword = context.getRealm().getPasswordPolicy().getPolicyConfig(NO_DOB_IN_PASSWORD_POLICY_PROVIDER_ID);
        UserModel userModel = context.getAuthenticationSession().getAuthenticatedUser() != null ? context.getAuthenticationSession().getAuthenticatedUser() : null;

        // Assigning dob based on login or registration
        String dob = userModel != null ? userModel.getFirstAttribute(DOB) : context.getAuthenticationSession().getAuthNote(DOB);

        if (dob != null) {
            String formattedDob = dob.replace("-", "");
            String[] dobParts = dob.split("-");

            String checkType = context.getRealm().getPasswordPolicy().getPolicyConfig(NO_DOB_IN_PASSWORD_POLICY_PROVIDER_ID);

            switch (checkType) {
                case FULL_DOB:
                    if (password.contains(formattedDob))
                        return new PolicyError(PASSWORD_CANNOT_CONTAIN_DATE_OF_BIRTH, noDobInPassword);
                    break;
                case DATE:
                case MONTH:
                case YEAR:
                    if (password.contains(dobParts[0]) || password.contains(dobParts[1]) || password.contains(dobParts[2]))
                        return new PolicyError(PASSWORD_CANNOT_CONTAIN_DATE_OF_BIRTH_PARTS, noDobInPassword);
                    break;
                default:
                    logger.info(UNIDENTIFIED_POLICY_INPUT);
                    break;
            }
        }

        return null;
    }


    @Override
    public Object parseConfig(String value) {
        return value;
    }

    @Override
    public void close() {
//      not needed
    }
}
