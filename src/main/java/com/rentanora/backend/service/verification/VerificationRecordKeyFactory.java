package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationChannel;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static com.rentanora.backend.constants.VerificationConstants.HASH_OPERATION_FAILED;
import static com.rentanora.backend.constants.VerificationStorageConstants.CHALLENGE_ID_PREFIX;
import static com.rentanora.backend.constants.VerificationStorageConstants.KEY_SEPARATOR;
import static com.rentanora.backend.constants.VerificationStorageConstants.SESSION_ID_PREFIX;
import static com.rentanora.backend.constants.VerificationStorageConstants.SESSION_TOKEN_HASH_ALGORITHM;

@Component
public class VerificationRecordKeyFactory {

    public String sessionRecordId(String sessionToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance(
                    SESSION_TOKEN_HASH_ALGORITHM
            );

            byte[] hash = digest.digest(
                    sessionToken.getBytes(StandardCharsets.UTF_8)
            );

            return SESSION_ID_PREFIX + HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    HASH_OPERATION_FAILED,
                    exception
            );
        }
    }

    public String challengeRecordId(
            String sessionId,
            VerificationChannel channel) {

        return CHALLENGE_ID_PREFIX
                + sessionId
                + KEY_SEPARATOR
                + channel.name();
    }
}