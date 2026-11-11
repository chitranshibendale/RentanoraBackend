package com.rentanora.backend.constants;

public final class AuthConstants {

    private AuthConstants() {
    }

    public static final String HEALTH_PATH = "/api/health";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String CURRENT_USER_PATH = "/api/auth/me";

    public static final String ADDITIONAL_AUTH_REQUIRED =
            "Additional authentication is required.";

    public static final String REDACTED_LOGIN_RESPONSE =
            "LoginResponse[accessToken=REDACTED]";

    public static final String COGNITO_REGION_PROPERTY =
            "${aws.cognito.region}";
    public static final String COGNITO_USER_POOL_ID_PROPERTY =
            "${aws.cognito.user-pool-id}";
    public static final String COGNITO_CLIENT_ID_PROPERTY =
            "${aws.cognito.client-id}";

    public static final String COGNITO_ISSUER_FORMAT =
            "https://cognito-idp.%s.amazonaws.com/%s";
    public static final String COGNITO_JWKS_PATH =
            "/.well-known/jwks.json";

    public static final String TOKEN_USE_CLAIM = "token_use";
    public static final String CLIENT_ID_CLAIM = "client_id";
    public static final String SUBJECT_CLAIM = "sub";
    public static final String EXPIRATION_CLAIM = "exp";
    public static final String ACCESS_TOKEN_USE = "access";

    public static final String SIGN_UP_PATH =
            "/api/auth/signup";

    public static final String CONFIRM_SIGN_UP_PATH =
            "/api/auth/confirm-signup";

    public static final String RESEND_CONFIRMATION_PATH =
            "/api/auth/resend-confirmation";

    public static final String EMAIL_ATTRIBUTE = "email";
    public static final String FIRST_NAME_ATTRIBUTE = "given_name";
    public static final String MIDDLE_NAME_ATTRIBUTE = "middle_name";
    public static final String LAST_NAME_ATTRIBUTE = "family_name";
    public static final String PHONE_ATTRIBUTE = "phone_number";
    public static final String CITY_ATTRIBUTE = "custom:city";
    public static final String STATE_ATTRIBUTE = "custom:State";
    public static final String COUNTRY_ATTRIBUTE = "custom:Country";
    public static final String PINCODE_ATTRIBUTE = "custom:pincode";
    public static final String REDACTED_SIGN_UP_REQUEST =
            "SignUpRequest[personalDetailsAndPassword=REDACTED]";
    public static final String PHONE_NUMBER_PATTERN =
            "^\\+[1-9][0-9]{1,14}$";

    public static final String INVALID_PHONE_NUMBER =
            "Enter your phone number with its country code, for example +917795485656.";

    public static final String INVALID_SIGN_UP_DETAILS =
            "Check your account details and try again.";

    public static final String PASSWORD_REQUIREMENTS_NOT_MET =
            "Your password does not meet the account password requirements.";

    public static final String ACCOUNT_ALREADY_EXISTS =
            "An account with this email already exists. Please sign in.";

    public static final String INVALID_CONFIRMATION_CODE =
            "The verification code is incorrect. Please try again.";

    public static final String EXPIRED_CONFIRMATION_CODE =
            "The verification code has expired. Request a new code.";

    public static final String TOO_MANY_AUTH_REQUESTS =
            "Too many attempts. Please wait before trying again.";

    public static final String AUTH_SERVICE_UNAVAILABLE =
            "Account services are temporarily unavailable. Please try again later.";

    public static final String EMAIL_CONFIRMATION_SUCCESS =
            "Your email has been verified. You can now sign in.";
}