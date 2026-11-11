package com.rentanora.backend.constants;

public final class VerificationConstants {

    private VerificationConstants() {
    }

    public static final String BASE_PATH =
            "/api/auth/verification";

    public static final String SEND_CODE_PATH =
            BASE_PATH + "/send-code";

    public static final String VERIFY_CODE_PATH =
            BASE_PATH + "/verify-code";

    public static final String EMAIL_CHANNEL = "email";
    public static final String PHONE_CHANNEL = "phone";

    public static final String CODE_PATTERN = "^[0-9]{6}$";

    public static final String INVALID_CODE =
            "Enter the six-digit verification code.";

    public static final String CODE_EXPIRED =
            "The verification code has expired. Request a new code.";

    public static final String CODE_INCORRECT =
            "The verification code is incorrect.";

    public static final String TOO_MANY_ATTEMPTS =
            "Too many attempts. Request a new verification code.";

    public static final String RESEND_TOO_SOON =
            "Please wait before requesting another verification code.";

    public static final String VERIFICATION_UNAVAILABLE =
            "Verification is temporarily unavailable. Please try again later.";

    public static final String BOTH_CONTACTS_REQUIRED =
            "Verify your phone number and email address before creating your account.";

    public static final String HASH_ALGORITHM = "HmacSHA256";

    public static final int MIN_HASH_SECRET_LENGTH = 64;

    public static final String INVALID_HASH_SECRET =
            "The verification security key is missing or invalid.";

    public static final String HASH_OPERATION_FAILED =
            "Unable to protect the verification code.";

    public static final String VERIFICATION_SESSION_EXPIRED =
            "Your verification session has expired. Please start again.";

    public static final String EMAIL_FORMAT_PATTERN =
            "^[^\\s@]+@[^\\s@.]+(?:\\.[^\\s@.]+)+$";

    public static final String PHONE_FORMAT_PATTERN =
            "^\\+[1-9][0-9]{1,14}$";

    public static final int MAX_EMAIL_LENGTH = 254;

    public static final String INVALID_EMAIL =
            "Enter a valid email address.";

    public static final String INVALID_PHONE =
            "Enter your phone number with its country code.";

    public static final String INVALID_CHANNEL =
            "Select email or phone verification.";
}