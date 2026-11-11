package com.rentanora.backend.mapper.verification;

import com.rentanora.backend.model.verification.VerificationChallenge;
import com.rentanora.backend.model.verification.VerificationChannel;
import com.rentanora.backend.model.verification.VerificationStatus;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

import static com.rentanora.backend.constants.VerificationConstants.VERIFICATION_UNAVAILABLE;
import static com.rentanora.backend.constants.VerificationStorageConstants.*;

@Component
public class VerificationChallengeMapper {

    public Map<String, AttributeValue> toItem(
            VerificationChallenge challenge) {

        return Map.ofEntries(
                Map.entry(ID, text(challenge.id())),
                Map.entry(SESSION_ID, text(challenge.sessionId())),
                Map.entry(CHALLENGE_ID, text(challenge.challengeId())),
                Map.entry(CHANNEL, text(challenge.channel().name())),
                Map.entry(DESTINATION, text(challenge.destination())),
                Map.entry(CODE_HASH, text(challenge.codeHash())),
                Map.entry(STATUS, text(challenge.status().name())),
                Map.entry(ATTEMPTS, number(challenge.attempts())),
                Map.entry(
                        CODE_EXPIRES_AT,
                        number(challenge.codeExpiresAt())
                ),
                Map.entry(
                        RESEND_AVAILABLE_AT,
                        number(challenge.resendAvailableAt())
                ),
                Map.entry(EXPIRES_AT, number(challenge.expiresAt()))
        );
    }

    public VerificationChallenge fromItem(
            Map<String, AttributeValue> item) {

        try {
            return new VerificationChallenge(
                    readText(item, ID),
                    readText(item, SESSION_ID),
                    readText(item, CHALLENGE_ID),
                    VerificationChannel.valueOf(
                            readText(item, CHANNEL)
                    ),
                    readText(item, DESTINATION),
                    readText(item, CODE_HASH),
                    VerificationStatus.valueOf(
                            readText(item, STATUS)
                    ),
                    Math.toIntExact(readNumber(item, ATTEMPTS)),
                    readNumber(item, CODE_EXPIRES_AT),
                    readNumber(item, RESEND_AVAILABLE_AT),
                    readNumber(item, EXPIRES_AT)
            );
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    VERIFICATION_UNAVAILABLE,
                    exception
            );
        }
    }

    private AttributeValue text(String value) {
        return AttributeValue.builder()
                .s(value)
                .build();
    }

    private AttributeValue number(long value) {
        return AttributeValue.builder()
                .n(Long.toString(value))
                .build();
    }

    private String readText(
            Map<String, AttributeValue> item,
            String name) {

        String value = item.get(name).s();

        if (value == null) {
            throw new IllegalStateException(VERIFICATION_UNAVAILABLE);
        }

        return value;
    }

    private long readNumber(
            Map<String, AttributeValue> item,
            String name) {

        return Long.parseLong(item.get(name).n());
    }
}