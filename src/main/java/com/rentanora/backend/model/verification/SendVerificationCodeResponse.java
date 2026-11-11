package com.rentanora.backend.model.verification;

public record SendVerificationCodeResponse(

        VerificationChannel channel,

        int expiresInSeconds,

        int resendAfterSeconds

) {
}