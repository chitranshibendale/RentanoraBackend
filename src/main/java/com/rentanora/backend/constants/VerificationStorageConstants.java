package com.rentanora.backend.constants;

public final class VerificationStorageConstants {

    private VerificationStorageConstants() {
    }

    public static final String CREATE_RECORD_CONDITION =
            "attribute_not_exists(#id)";

    public static final String CONSUMED = "consumed";

    public static final String SESSION_ID_PREFIX = "session#";

    public static final String CHALLENGE_ID_PREFIX = "challenge#";

    public static final String KEY_SEPARATOR = "#";

    public static final String SESSION_TOKEN_HASH_ALGORITHM =
            "SHA-256";

    public static final String CHALLENGE_ID_ALIAS = "#challengeId";
    public static final String STATUS_ALIAS = "#status";
    public static final String ATTEMPTS_ALIAS = "#attempts";
    public static final String EXPIRES_AT_ALIAS = "#expiresAt";

    public static final String EXPECTED_CHALLENGE_ID_VALUE =
            ":expectedChallengeId";

    public static final String EXPECTED_STATUS_VALUE =
            ":expectedStatus";

    public static final String EXPECTED_ATTEMPTS_VALUE =
            ":expectedAttempts";

    public static final String NOW_VALUE = ":now";

    public static final String REPLACE_CHALLENGE_CONDITION =
            "#challengeId = :expectedChallengeId"
                    + " AND #status = :expectedStatus"
                    + " AND #attempts = :expectedAttempts"
                    + " AND #expiresAt > :now";

    public static final String ID = "id";

    public static final String SESSION_ID = "sessionId";

    public static final String CHALLENGE_ID = "challengeId";

    public static final String CHANNEL = "channel";

    public static final String DESTINATION = "destination";

    public static final String CODE_HASH = "codeHash";

    public static final String STATUS = "status";

    public static final String ATTEMPTS = "attempts";

    public static final String CODE_EXPIRES_AT = "codeExpiresAt";

    public static final String RESEND_AVAILABLE_AT = "resendAvailableAt";

    public static final String EXPIRES_AT = "expiresAt";

    public static final String ID_ALIAS = "#id";

    public static final String CREATE_CHALLENGE_CONDITION =
            "attribute_not_exists(#id)";
}