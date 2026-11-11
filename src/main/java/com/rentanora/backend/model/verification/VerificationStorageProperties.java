package com.rentanora.backend.model.verification;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "aws.verification")
public record VerificationStorageProperties(

        @NotBlank
        String region,

        @NotBlank
        String tableName,

        @NotBlank
        String hashSecretId

) {
}