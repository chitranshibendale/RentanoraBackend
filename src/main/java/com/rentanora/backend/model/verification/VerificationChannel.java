package com.rentanora.backend.model.verification;

import com.fasterxml.jackson.annotation.JsonProperty;

import static com.rentanora.backend.constants.VerificationConstants.EMAIL_CHANNEL;
import static com.rentanora.backend.constants.VerificationConstants.PHONE_CHANNEL;

public enum VerificationChannel {

    @JsonProperty(EMAIL_CHANNEL)
    EMAIL,

    @JsonProperty(PHONE_CHANNEL)
    PHONE
}