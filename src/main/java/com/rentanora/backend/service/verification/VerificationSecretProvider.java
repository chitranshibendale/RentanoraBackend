package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationStorageProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.rentanora.backend.constants.VerificationConstants.HASH_ALGORITHM;
import static com.rentanora.backend.constants.VerificationConstants.INVALID_HASH_SECRET;
import static com.rentanora.backend.constants.VerificationConstants.MIN_HASH_SECRET_LENGTH;

@Component
public class VerificationSecretProvider {

    private final SecretsManagerClient secretsManagerClient;
    private final VerificationStorageProperties properties;

    private SecretKey cachedKey;

    public VerificationSecretProvider(
            SecretsManagerClient secretsManagerClient,
            VerificationStorageProperties properties) {

        this.secretsManagerClient = secretsManagerClient;
        this.properties = properties;
    }

    public synchronized SecretKey getKey() {
        if (cachedKey != null) {
            return cachedKey;
        }

        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(properties.hashSecretId())
                .build();

        String secret = secretsManagerClient
                .getSecretValue(request)
                .secretString();

        if (secret == null || secret.length() < MIN_HASH_SECRET_LENGTH) {
            throw new IllegalStateException(INVALID_HASH_SECRET);
        }

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        try {
            cachedKey = new SecretKeySpec(keyBytes, HASH_ALGORITHM);
            return cachedKey;
        } finally {
            Arrays.fill(keyBytes, (byte) 0);
        }
    }
}