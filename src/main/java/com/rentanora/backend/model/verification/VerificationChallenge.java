package com.rentanora.backend.model.verification;

import lombok.Builder;

@Builder(toBuilder = true)
public record VerificationChallenge(
        String id,
        String sessionId,
        String challengeId,
        VerificationChannel channel,
        String destination,
        String codeHash,
        VerificationStatus status,
        int attempts,
        long codeExpiresAt,
        long resendAvailableAt,
        long expiresAt
) {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}