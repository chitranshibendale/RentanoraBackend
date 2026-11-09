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
}