package com.rentanora.backend.model.verification;

public record VerifyCodeResponse(

        VerificationChannel channel,

        boolean verified

) { }