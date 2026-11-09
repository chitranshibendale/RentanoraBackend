package com.rentanora.backend.config;

import static com.rentanora.backend.constants.AuthConstants.*;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class CognitoJwtConfig {

    @Bean
    public JwtDecoder jwtDecoder(
            @Value(COGNITO_REGION_PROPERTY) String region,
            @Value(COGNITO_USER_POOL_ID_PROPERTY) String userPoolId,
            @Value(COGNITO_CLIENT_ID_PROPERTY) String clientId) {

        String issuer = COGNITO_ISSUER_FORMAT.formatted(
                region,
                userPoolId
        );

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri(issuer + COGNITO_JWKS_PATH)
                .build();

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefaultWithIssuer(issuer),
                new JwtClaimValidator<String>(
                        TOKEN_USE_CLAIM, ACCESS_TOKEN_USE::equals),
                new JwtClaimValidator<String>(
                        CLIENT_ID_CLAIM, clientId::equals),
                new JwtClaimValidator<String>(
                        SUBJECT_CLAIM,
                        value -> value != null && !value.isBlank()),
                new JwtClaimValidator<Instant>(
                        EXPIRATION_CLAIM, value -> value != null)
        ));

        return decoder;
    }
}