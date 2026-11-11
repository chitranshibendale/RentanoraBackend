package com.rentanora.backend.model.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.rentanora.backend.constants.VerificationConstants.CODE_PATTERN;
import static com.rentanora.backend.constants.VerificationConstants.INVALID_CODE;

public record VerifyCodeRequest(

        @NotNull
        VerificationChannel channel,

        @NotBlank
        @Size(max = 254)
        String destination,

        @NotBlank(message = INVALID_CODE)
        @Pattern(
                regexp = CODE_PATTERN,
                message = INVALID_CODE
        )
        String code

) {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}