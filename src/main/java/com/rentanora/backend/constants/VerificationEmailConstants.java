package com.rentanora.backend.constants;

public final class VerificationEmailConstants {

    private VerificationEmailConstants() {
    }

    public static final String VERIFICATION_SUBJECT =
            "Verify your email address | Rentanora";

    public static final String VERIFICATION_BODY = """
            Your Rentanora verification code is:

            %s

            Enter this code on Rentanora to verify your email address.
            Do not share this code with anyone.

            If you did not request this code, ignore this email.
            """;
}