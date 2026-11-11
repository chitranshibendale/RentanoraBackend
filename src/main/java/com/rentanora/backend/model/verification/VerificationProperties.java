package com.rentanora.backend.model.verification;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "verification")
public record VerificationProperties(

        @Min(6)
        @Max(6)
        int codeLength,

        @Min(60)
        @Max(900)
        int codeExpirySeconds,

        @Min(60)
        int resendCooldownSeconds,

        @Min(1)
        @Max(5)
        int maxAttempts,

        @Min(600)
        @Max(3600)
        int sessionExpirySeconds

) {
}