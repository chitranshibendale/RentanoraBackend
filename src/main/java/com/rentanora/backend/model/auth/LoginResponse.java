package com.rentanora.backend.model.auth;

import static com.rentanora.backend.constants.AuthConstants.REDACTED_LOGIN_RESPONSE;

public record LoginResponse(
        String accessToken,
        Integer expiresIn) {

    @Override
    public String toString() {
        return REDACTED_LOGIN_RESPONSE;
    }
}