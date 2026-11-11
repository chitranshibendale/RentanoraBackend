package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationProperties;
import com.rentanora.backend.model.verification.VerificationSession;
import com.rentanora.backend.repository.verification.VerificationSessionRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;

import static com.rentanora.backend.constants.VerificationConstants.VERIFICATION_UNAVAILABLE;

@Service
public class VerificationSessionService {

    private static final int SESSION_TOKEN_LENGTH = 43;
    private static final int CREATION_ATTEMPTS = 3;

    private final VerificationSessionRepository repository;
    private final VerificationTokenGenerator tokenGenerator;
    private final VerificationRecordKeyFactory keyFactory;
    private final VerificationProperties properties;
    private final Clock clock;

    public VerificationSessionService(
            VerificationSessionRepository repository,
            VerificationTokenGenerator tokenGenerator,
            VerificationRecordKeyFactory keyFactory,
            VerificationProperties properties,
            Clock clock) {

        this.repository = repository;
        this.tokenGenerator = tokenGenerator;
        this.keyFactory = keyFactory;
        this.properties = properties;
        this.clock = clock;
    }

    public String createSession() {
        long expiresAt = clock.instant()
                .plusSeconds(properties.sessionExpirySeconds())
                .getEpochSecond();

        for (int attempt = 0; attempt < CREATION_ATTEMPTS; attempt++) {
            String token = tokenGenerator.generate();

            VerificationSession session = VerificationSession.builder()
                    .id(keyFactory.sessionRecordId(token))
                    .expiresAt(expiresAt)
                    .consumed(false)
                    .build();

            if (repository.createIfAbsent(session)) {
                return token;
            }
        }

        throw new IllegalStateException(VERIFICATION_UNAVAILABLE);
    }

    public Optional<VerificationSession> findActive(String token) {
        if (token == null || token.length() != SESSION_TOKEN_LENGTH) {
            return Optional.empty();
        }

        String id = keyFactory.sessionRecordId(token);
        long now = clock.instant().getEpochSecond();

        return repository.findById(id)
                .filter(session -> !session.consumed())
                .filter(session -> session.expiresAt() > now);
    }
}