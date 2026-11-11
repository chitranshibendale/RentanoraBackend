package com.rentanora.backend.mapper.verification;

import com.rentanora.backend.model.verification.VerificationSession;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.Objects;

import static com.rentanora.backend.constants.VerificationConstants.VERIFICATION_UNAVAILABLE;
import static com.rentanora.backend.constants.VerificationStorageConstants.CONSUMED;
import static com.rentanora.backend.constants.VerificationStorageConstants.EXPIRES_AT;
import static com.rentanora.backend.constants.VerificationStorageConstants.ID;

@Component
public class VerificationSessionMapper {

    public Map<String, AttributeValue> toItem(
            VerificationSession session) {

        return Map.of(
                ID,
                AttributeValue.builder()
                        .s(session.id())
                        .build(),

                EXPIRES_AT,
                AttributeValue.builder()
                        .n(Long.toString(session.expiresAt()))
                        .build(),

                CONSUMED,
                AttributeValue.builder()
                        .bool(session.consumed())
                        .build()
        );
    }

    public VerificationSession fromItem(
            Map<String, AttributeValue> item) {

        try {
            return VerificationSession.builder()
                    .id(
                            Objects.requireNonNull(
                                    item.get(ID).s()
                            )
                    )
                    .expiresAt(
                            Long.parseLong(item.get(EXPIRES_AT).n())
                    )
                    .consumed(
                            Objects.requireNonNull(
                                    item.get(CONSUMED).bool()
                            )
                    )
                    .build();
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    VERIFICATION_UNAVAILABLE,
                    exception
            );
        }
    }
}