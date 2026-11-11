package com.rentanora.backend.model.verification;

import lombok.Builder;

@Builder(toBuilder = true)
public record VerificationSession(
        String id,
        long expiresAt,
        boolean consumed
) {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}