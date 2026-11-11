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
}