package org.example.authenticator.utils;

import java.time.Duration;

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
    public static final String USERNAME = "username";

    //     forms
    public static final String VERIFY_OTP_PAGE = "verify-otp.ftl";
    public static final String REGISTER_PAGE = "register.ftl";
    public static final String LOGIN_PAGE = "login.ftl";
    public static final String FORGOT_PASSWORD_PAGE = "forgot-password.ftl";
    public static final String UPDATE_PASSWORD_PAGE = "update-password.ftl";

    // User
    public static final String TEMP_USER_NAME = "TEMP_USER_NAME";
    public static final String TEMP_PASSWORD = "TEMP_PASSWORD";
    public static final String TEMP_DOB = "TEMP_DOB";
    public static final String PASSWORD = "password";
    public static final String CONFIRM_PASSWORD = "confirmPassword";
    public static final String PASSWORD_LAST_CHANGED = "passwordLastChanged";
    public static final String DOB = "dob";

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
    public static final String USER_TEMPORARILY_DISABLED = "User temporarily disabled.";

    // Required actions
    public static final int PASSWORD_EXPIRY_DAYS = 1;
    public static final String IS_REMEMBER_ME_ALLOWED = "isRememberMeAllowed";
    public static final String SMS_PROVIDER_ID = "sms-form";
    public static final String REGISTER_PROVIDER_ID = "registration using mobile number";
    public static final String ALGORITHM = "RS256";
    public static final String JWT = "JWT";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    public static final String HEADER_VALUE = "application/json";
    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-type";
    public static final String SIGNUP_USER_API = "SIGNUP_USER_API";
    public static final String SIGN_UP_PATH = "/signUp";
    public static final String PASSWORD_POLICY_ERROR = "Password must have a upper case letter, a special character,";
    public static final int MAX_OTP_RESEND_ATTEMPTS = 3;
    public static final String OTP_RESEND_COUNT = "otpResendCount";
    public static final String OTP_LAST_RESEND_TIME = "otpLastSendTime";
    public static final long OTP_RESEND_COOLDOWN_DURATION = Duration.ofMinutes(5).toMillis();
    public static final String WAIT_FOR_OTP_RESEND_MESSAGE = "Please wait until %d seconds before resending otp.";
    public static final String PASSWORD_STARTS_WITH_SPECIAL_CHAR_PROVIDER_ID = "startsWithSpecialChar";
    public static final String PASSWORD_MUST_STARTS_WITH_SPECIAL_CHAR = "Password must starts with the special character.";
    public static final String NO_REPETITIVE_PASSWORD_POLICY_PROVIDER_ID = "noRepetitiveChars";
    public static final String REPEATED_CHARACTERS_PRESENT_IN_PASSWORD = "Password contains more than specified repetitive characters.";
    public static final String FULL_DOB = "fullDob";
    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String YEAR = "year";
    public static final String  NO_DOB_IN_PASSWORD_POLICY_PROVIDER_ID = "noDateOfBirthAllowed";
    public static final String PASSWORD_CANNOT_CONTAIN_DATE_OF_BIRTH = "Password cannot contains your full date of birth";
    public static final String UNIDENTIFIED_POLICY_INPUT = "Unidentified value present in no dob password policy input";
    public static final String  PASSWORD_CANNOT_CONTAIN_DATE_OF_BIRTH_PARTS = "Password cannot contain any part of the date of birth";
}
