package com.rentanora.backend.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmSignUpRequest(

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(max = 2048)
        String code

) {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}