package org.example.authenticator.utils;

public class Constants {

    private Constants() {
        // intentionally set to empty
    }

    //    flow_types
    public static final String LOGIN_FLOW = "login";
    public static final String REGISTER_FLOW = "register";
    public static final String RESET_PASSWORD_FLOW = "resetPassword";
    public static final String INVALID_FLOW_TYPE = "Invalid flow type";

    //    SMS
    public static final String OTP_SESSION_ATTRIBUTE = "otp";
    public static final String RESEND_OTP = "resend";
    public static final String OTP_REQUIRED = "OTP required!";
    public static final String OTP_CREATION_TIME_ATTRIBUTE = "otpCreationTime";
    public static final long OTP_VALIDITY_DURATION = 60000;
    public static final String INVALID_OTP = "Invalid OTP";
    public static final String OTP_EXPIRED = "OTP has expired. Please request a new OTP.";
    public static final String MOBILE_NUMBER = "mobileNumber";
    public static final String OTP_SENT = "OTP sent successfully";

    //     forms
    public static final String VERIFY_OTP_PAGE = "verify-otp.ftl";
    public static final String REGISTER_PAGE = "register.ftl";
    public static final String LOGIN_PAGE = "login.ftl";
    public static final String FORGOT_PASSWORD_PAGE = "forgot-password.ftl";
    public static final String UPDATE_PASSWORD_PAGE = "update-password.ftl";

    // User
    public static final String TEMP_USER_NAME = "TEMP_USER_NAME";
    public static final String TEMP_PASSWORD = "TEMP_PASSWORD";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String PASSWORD_LAST_CHANGED = "passwordLastChanged";

    // Errors
    public static final String INTERNAL_ERROR = "Internal server error";
    public static final String MOBILE_NUMBER_NULL = "Mobile number is missing. Restart the login process.";
    public static final String USER_NOT_FOUND = "User not found or mobile number is missing";
    public static final String OTP_SENT_FAILED = "Internal Server Error, OTP sent failed.";
    public static final String REQUIRED_FIELDS = "Mobile number and password required";
    public static final String INVALID_CREDENTIALS = "Invalid credentials!";
    public static final String USER_EXISTS = "Mobile number already exists!";
    public static final String NO_MATCHING_PASSWORD = "Entered password doesn't matches with confirm password!";
    public static final String OTP_SEND_FAILED = "OTP sent failed.";
    public static final String PASSWORD_REQUIRED = "Password required.";

    // Required actions
    public static final int PASSWORD_EXPIRY_DAYS = 1;
    public static final String IS_REMEMBER_ME_ALLOWED = "isRememberMeAllowed";
    public static final String SMS_PROVIDER_ID = "sms-form";
}
