package com.rentanora.backend.model.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static com.rentanora.backend.constants.AuthConstants.REDACTED_SIGN_UP_REQUEST;

public record SignUpRequest(

        @NotBlank
        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String middleName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotBlank
        @Size(max = 32)
        String phone,

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(max = 50)
        String city,

        @NotBlank
        @Size(max = 50)
        String state,

        @NotBlank
        @Size(max = 50)
        String country,

        @NotBlank
        @Size(max = 16)
        String pincode,

        @NotBlank
        @Size(max = 256)
        String password

) {

    @Override
    public String toString() {
        return REDACTED_SIGN_UP_REQUEST;
    }
}