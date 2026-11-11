package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class VerificationCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    private final int codeLength;

    public VerificationCodeGenerator(
            VerificationProperties properties) {

        this.codeLength = properties.codeLength();
    }

    public String generate() {
        StringBuilder code = new StringBuilder(codeLength);

        for (int index = 0; index < codeLength; index++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }
}