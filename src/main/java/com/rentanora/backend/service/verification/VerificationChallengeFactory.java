package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationChallenge;
import com.rentanora.backend.model.verification.VerificationChannel;
import com.rentanora.backend.model.verification.VerificationProperties;
import com.rentanora.backend.model.verification.VerificationSession;
import com.rentanora.backend.model.verification.VerificationStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;

import static com.rentanora.backend.constants.VerificationConstants.VERIFICATION_SESSION_EXPIRED;

@Component
public class VerificationChallengeFactory {

    private final VerificationRecordKeyFactory keyFactory;
    private final VerificationTokenGenerator tokenGenerator;
    private final VerificationCodeHasher codeHasher;
    private final VerificationContactNormalizer contactNormalizer;
    private final VerificationProperties properties;
    private final Clock clock;

    public VerificationChallengeFactory(
            VerificationRecordKeyFactory keyFactory,
            VerificationTokenGenerator tokenGenerator,
            VerificationCodeHasher codeHasher,
            VerificationContactNormalizer contactNormalizer,
            VerificationProperties properties,
            Clock clock) {

        this.keyFactory = keyFactory;
        this.tokenGenerator = tokenGenerator;
        this.codeHasher = codeHasher;
        this.contactNormalizer = contactNormalizer;
        this.properties = properties;
        this.clock = clock;
    }

    public VerificationChallenge create(
            VerificationSession session,
            VerificationChannel channel,
            String destination,
            String code) {

        long now = clock.instant().getEpochSecond();

        if (session == null
                || session.consumed()
                || session.expiresAt() <= now) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    VERIFICATION_SESSION_EXPIRED
            );
        }

        String normalizedDestination =
                contactNormalizer.normalize(channel, destination);

        String challengeId = tokenGenerator.generate();

        String codeHash = codeHasher.hash(
                session.id(),
                challengeId,
                channel,
                normalizedDestination,
                code
        );

        return VerificationChallenge.builder()
                .id(keyFactory.challengeRecordId(session.id(), channel))
                .sessionId(session.id())
                .challengeId(challengeId)
                .channel(channel)
                .destination(normalizedDestination)
                .codeHash(codeHash)
                .status(VerificationStatus.PENDING_DELIVERY)
                .attempts(0)
                .codeExpiresAt(Math.min(
                        now + properties.codeExpirySeconds(),
                        session.expiresAt()
                ))
                .resendAvailableAt(
                        now + properties.resendCooldownSeconds()
                )
                .expiresAt(session.expiresAt())
                .build();
    }
}