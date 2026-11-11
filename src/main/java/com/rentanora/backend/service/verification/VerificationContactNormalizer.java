package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationChannel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

import static com.rentanora.backend.constants.VerificationConstants.EMAIL_FORMAT_PATTERN;
import static com.rentanora.backend.constants.VerificationConstants.INVALID_CHANNEL;
import static com.rentanora.backend.constants.VerificationConstants.INVALID_EMAIL;
import static com.rentanora.backend.constants.VerificationConstants.INVALID_PHONE;
import static com.rentanora.backend.constants.VerificationConstants.MAX_EMAIL_LENGTH;
import static com.rentanora.backend.constants.VerificationConstants.PHONE_FORMAT_PATTERN;

@Component
public class VerificationContactNormalizer {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(EMAIL_FORMAT_PATTERN);

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(PHONE_FORMAT_PATTERN);

    public String normalize(
            VerificationChannel channel,
            String destination) {

        if (channel == null) {
            throw invalidContact(INVALID_CHANNEL);
        }

        String normalized =
                destination == null ? "" : destination.strip();

        return switch (channel) {
            case EMAIL -> normalizeEmail(normalized);
            case PHONE -> normalizePhone(normalized);
        };
    }

    private String normalizeEmail(String email) {
        if (email.length() > MAX_EMAIL_LENGTH
                || !EMAIL_PATTERN.matcher(email).matches()) {

            throw invalidContact(INVALID_EMAIL);
        }

        return email;
    }

    private String normalizePhone(String phone) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw invalidContact(INVALID_PHONE);
        }

        return phone;
    }

    private ResponseStatusException invalidContact(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}