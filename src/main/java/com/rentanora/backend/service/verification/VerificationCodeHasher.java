package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationChannel;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;

import static com.rentanora.backend.constants.VerificationConstants.HASH_ALGORITHM;
import static com.rentanora.backend.constants.VerificationConstants.HASH_OPERATION_FAILED;

@Component
public class VerificationCodeHasher {

    private final VerificationSecretProvider secretProvider;

    public VerificationCodeHasher(
            VerificationSecretProvider secretProvider) {

        this.secretProvider = secretProvider;
    }

    public String hash(
            String sessionId,
            String challengeId,
            VerificationChannel channel,
            String destination,
            String code) {

        try {
            Mac mac = Mac.getInstance(HASH_ALGORITHM);
            mac.init(secretProvider.getKey());

            update(mac, sessionId);
            update(mac, challengeId);
            update(mac, channel.name());
            update(mac, destination);
            update(mac, code);

            return HexFormat.of().formatHex(mac.doFinal());
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(
                    HASH_OPERATION_FAILED,
                    exception
            );
        }
    }

    public boolean matches(
            String expectedHash,
            String sessionId,
            String challengeId,
            VerificationChannel channel,
            String destination,
            String code) {

        if (expectedHash == null) {
            return false;
        }

        String actualHash = hash(
                sessionId,
                challengeId,
                channel,
                destination,
                code
        );

        return MessageDigest.isEqual(
                expectedHash.getBytes(StandardCharsets.US_ASCII),
                actualHash.getBytes(StandardCharsets.US_ASCII)
        );
    }

    private void update(Mac mac, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

        mac.update(
                ByteBuffer.allocate(Integer.BYTES)
                        .putInt(bytes.length)
                        .array()
        );

        mac.update(bytes);
    }
}