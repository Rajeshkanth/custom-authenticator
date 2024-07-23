package org.example.authenticator.sms;

import jakarta.ws.rs.core.MultivaluedMap;
import org.example.authenticator.utils.OtpUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.authenticator.onboard.OnBoardingViaMobile.triggerRegisterEvent;
import static org.example.authenticator.utils.Constants.*;
import static org.example.authenticator.utils.FailureChallenge.showError;

public class SMSAuthenticator implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(SMSAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.info("Entered in SMS authenticate.");
        String mobileNumber = context.getUser() != null ? context.getUser().getUsername() : null;

        if (mobileNumber != null) {
            processOTP(context, mobileNumber, LOGIN_PAGE);
        } else {
            logger.info("OTP for registration");
            String regMobileNumber = context.getAuthenticationSession().getAuthNote(TEMP_USER_NAME);
            processOTP(context, regMobileNumber, REGISTER_PAGE);
        }

        context.challenge(context.form().createForm(VERIFY_OTP_PAGE));
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        logger.info("Entered in SMS auth action.");
        MultivaluedMap<String, String> formParams = context.getHttpRequest().getDecodedFormParameters();
        String otp = formParams.getFirst(OTP_SESSION_ATTRIBUTE);
        String resendOtp = formParams.getFirst(RESEND_OTP);
        String flowType = determineFlowType(context);

        logger.info("Flow type, {} ", flowType);

        switch (flowType) {
            case LOGIN_FLOW:
                logger.info("Login flow is running.");
                handleOtpAction(context, otp, resendOtp, flowType, LOGIN_PAGE);
                break;
            case REGISTER_FLOW:
                logger.info("Register flow is running.");
                handleOtpAction(context, otp, resendOtp, flowType, REGISTER_PAGE);
                break;
            case RESET_PASSWORD_FLOW:
                logger.info("Reset password flow is running.");
                handleOtpAction(context, otp, resendOtp, flowType, FORGOT_PASSWORD_PAGE);
                break;
            default:
                logger.info("Invalid flow.");
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_FLOW_TYPE, VERIFY_OTP_PAGE);
                break;
        }
    }

    private void processOTP(AuthenticationFlowContext context, String mobileNumber, String form) {
        try {
            String generatedOtp = OtpUtils.generateOTP(6);
            boolean isOtpSent = OtpUtils.sendOTP(mobileNumber, generatedOtp, context, form);
            if (isOtpSent) {
                logger.info("Otp sent.");
                context.getAuthenticationSession().setAuthNote(OTP_SESSION_ATTRIBUTE, generatedOtp);
                context.getAuthenticationSession().setAuthNote(OTP_CREATION_TIME_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
            } else {
                showError(context, AuthenticationFlowError.INTERNAL_ERROR, OTP_SEND_FAILED, form);
            }
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INTERNAL_ERROR, INTERNAL_ERROR, form);
        }
    }

    private String determineFlowType(AuthenticationFlowContext context) {
        logger.info("Determining the flow type for SMS.");
        String currentExecutionId = context.getExecution().getId();
        AuthenticationExecutionModel currentExecution = context.getRealm().getAuthenticationExecutionById(currentExecutionId);

        String parentFlowId = currentExecution.getParentFlow();
        AuthenticationFlowModel parentFlow = context.getRealm().getAuthenticationFlowById(parentFlowId);
        List<AuthenticationExecutionModel> executions = context.getRealm().getAuthenticationExecutionsStream(parentFlowId).collect(Collectors.toList());
        logger.info(parentFlow.getProviderId());

        for (AuthenticationExecutionModel execution : executions) {
            switch (execution.getAuthenticator()) {
                case LOGIN_PROVIDER_ID:
                    return LOGIN_FLOW;
                case RESET_CRED_PROVIDER_ID:
                    return RESET_PASSWORD_FLOW;
                case REGISTER_PROVIDER_ID:
                    return REGISTER_FLOW;
                default:
                    break;
            }
        }

        return INVALID_FLOW_TYPE;
    }

    private void handleOtpAction(AuthenticationFlowContext context, String otp, String resend, String flowType, String form) {
        if (resend != null) {
            logger.debug("Resending otp for {}", flowType);
            handleOtpResend(context, form);
        } else if (otp != null) {
            logger.debug("Validating otp for {}", flowType);
            handleOtpValidation(context, flowType, otp);
        } else {
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, OTP_REQUIRED, VERIFY_OTP_PAGE);
        }
    }

    private void handleOtpValidation(AuthenticationFlowContext context, String flowType, String otp) {
        String sessionOtp = context.getAuthenticationSession().getAuthNote(OTP_SESSION_ATTRIBUTE);
        String otpCreationTimeStr = context.getAuthenticationSession().getAuthNote(OTP_CREATION_TIME_ATTRIBUTE);

        if (sessionOtp == null || otpCreationTimeStr == null) {
            logger.error(INVALID_OTP);
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_OTP, VERIFY_OTP_PAGE);
            return;
        }

        long otpCreationTime = Long.parseLong(otpCreationTimeStr);
        if (System.currentTimeMillis() - otpCreationTime > OTP_VALIDITY_DURATION) {
            logger.error(OTP_EXPIRED);
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, OTP_EXPIRED, VERIFY_OTP_PAGE);
            return;
        }

        switch (flowType) {
            case LOGIN_FLOW:
            case RESET_PASSWORD_FLOW:
                UserModel authenticatedUser = context.getAuthenticationSession().getAuthenticatedUser();
                if (otp.equals(sessionOtp) && authenticatedUser != null) {
                    context.setUser(authenticatedUser);
                    context.success();
                } else {
                    showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_OTP, VERIFY_OTP_PAGE);
                }
                break;
            case REGISTER_FLOW:
                if (otp.equals(sessionOtp)) {
                    createUserAndAuthenticate(context);
                } else {
                    showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_OTP, VERIFY_OTP_PAGE);
                }
                break;
            default:
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INVALID_FLOW_TYPE, LOGIN_PAGE);
                break;
        }

    }

    private void createUserAndAuthenticate(AuthenticationFlowContext context) {
        String userName = context.getAuthenticationSession().getAuthNote(TEMP_USER_NAME);
        String dob = context.getAuthenticationSession().getAuthNote(TEMP_DOB);

        try {
            UserModel newUser = context.getSession().users().addUser(context.getRealm(), userName);
            newUser.setEnabled(true);
            newUser.setSingleAttribute(DOB, dob);
            context.setUser(newUser);
            context.getAuthenticationSession().setAuthenticatedUser(newUser);
            newUser.setSingleAttribute(LAST_LOGIN, LocalDate.now().toString());
            triggerRegisterEvent(context, newUser);
            context.success();
            logger.info("User {} created successfully", userName);
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, INTERNAL_ERROR, VERIFY_OTP_PAGE);
        }
    }

    private void handleOtpResend(AuthenticationFlowContext context, String form) {
        UserModel user = context.getAuthenticationSession().getAuthenticatedUser();
        String resendOtpCountStr = context.getAuthenticationSession().getAuthNote(OTP_RESEND_COUNT);
        String lastResendOtpTimeStr = context.getAuthenticationSession().getAuthNote(OTP_LAST_RESEND_TIME);

        int resendOtpCount = resendOtpCountStr != null ? Integer.parseInt(resendOtpCountStr) : 0;
        long lastResendTime = lastResendOtpTimeStr != null ? Long.parseLong(lastResendOtpTimeStr) : 0;
        long currentTime = System.currentTimeMillis();

        if ((currentTime - lastResendTime) < OTP_RESEND_COOLDOWN_DURATION && resendOtpCount >= MAX_OTP_RESEND_ATTEMPTS) {
            logger.error("OTP resend limit reached.");
            long waitTimeInSeconds = OTP_RESEND_COOLDOWN_DURATION - (currentTime - lastResendTime);
            String waitTimeMessage = String.format(WAIT_FOR_OTP_RESEND_MESSAGE, waitTimeInSeconds / 1000);
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, waitTimeMessage, form);
            return;
        }

        if (resendOtpCount >= MAX_OTP_RESEND_ATTEMPTS) {
            resendOtpCount = 0; // Reset count after cooldown period
        }

        if (user != null) {
            String mobileNumber = user.getUsername();

            if (mobileNumber == null) {
                logger.info("Mobile number is null.");
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, MOBILE_NUMBER_NULL, form);
                return;
            }

            String generatedOtp = OtpUtils.generateOTP(6);
            boolean otpSent = OtpUtils.sendOTP(mobileNumber, generatedOtp, context, VERIFY_OTP_PAGE);

            if (otpSent) {
                logger.info("OTP resent successfully.");
                context.getAuthenticationSession().setAuthNote(OTP_SESSION_ATTRIBUTE, generatedOtp);
                context.getAuthenticationSession().setAuthNote(OTP_CREATION_TIME_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
                context.getAuthenticationSession().setAuthNote(OTP_RESEND_COUNT, String.valueOf(resendOtpCount + 1));
                context.getAuthenticationSession().setAuthNote(OTP_LAST_RESEND_TIME, String.valueOf(currentTime));
                context.challenge(context.form().setSuccess(OTP_SENT).createForm(VERIFY_OTP_PAGE));
            } else {
                logger.info("OTP send failed.");
                showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, OTP_SENT_FAILED, VERIFY_OTP_PAGE);
            }
        } else {
            logger.error("User not found.");
            showError(context, AuthenticationFlowError.INVALID_CREDENTIALS, USER_NOT_FOUND, LOGIN_PAGE);
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
        // No required actions needed
    }

    @Override
    public void close() {
        // No resources to close
    }
}
