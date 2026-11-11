package com.rentanora.backend.model.verification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "aws.verification.email")
public record VerificationEmailProperties(

        @NotBlank
        @Email
        String fromAddress

) {
}