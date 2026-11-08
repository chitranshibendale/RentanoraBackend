package com.rentanora.backend.auth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GetUserRequest;

@Service
public class AuthService {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String clientId;
    private final String clientSecret;

    public AuthService(
            CognitoIdentityProviderClient cognitoClient,
            @Value("${aws.cognito.client-id}") String clientId,
            @Value("${aws.cognito.client-secret}") String clientSecret) {

        this.cognitoClient = cognitoClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String authenticateUser(LoginRequest loginRequest) {
        InitiateAuthResponse response = authenticate(loginRequest);

        if (response.authenticationResult() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Additional authentication is required."
            );
        }

        var user = cognitoClient.getUser(
                GetUserRequest.builder()
                        .accessToken(
                                response.authenticationResult().accessToken()
                        )
                        .build()
        );

        return user.userAttributes().stream()
                .filter(attribute -> "sub".equals(attribute.name()))
                .map(attribute -> attribute.value())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user identifier is missing."
                ));
    }

    public InitiateAuthResponse authenticate(LoginRequest loginRequest) {
        String email = loginRequest.email();

        String secretHash = CognitoSecretHash.calculate(
                email,
                clientId,
                clientSecret
        );

        InitiateAuthRequest request = InitiateAuthRequest.builder()
                .clientId(clientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        "USERNAME", email,
                        "PASSWORD", loginRequest.password(),
                        "SECRET_HASH", secretHash
                ))
                .build();

        try {
            return cognitoClient.initiateAuth(request);
        } catch (NotAuthorizedException | UserNotFoundException exception) {
            throw new InvalidCredentialsException();
        }
    }
}