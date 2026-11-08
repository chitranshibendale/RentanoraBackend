package com.rentanora.backend.auth;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class CognitoSecretHash {

    private static final String ALGORITHM = "HmacSHA256";

    private CognitoSecretHash() {
    }

    public static String calculate(
            String username,
            String clientId,
            String clientSecret) {

        try {
            Mac mac = Mac.getInstance(ALGORITHM);

            SecretKeySpec key = new SecretKeySpec(
                    clientSecret.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );

            mac.init(key);

            byte[] hash = mac.doFinal(
                    (username + clientId).getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder().encodeToString(hash);

        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    "Unable to calculate Cognito client-secret hash.",
                    exception
            );
        }
    }
}