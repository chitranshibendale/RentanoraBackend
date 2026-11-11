package com.rentanora.backend.model.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendVerificationCodeRequest(

        @NotNull
        VerificationChannel channel,

        @NotBlank
        @Size(max = 254)
        String destination

) {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}