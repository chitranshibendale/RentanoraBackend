package com.rentanora.backend.auth;

import java.util.Map;

import com.rentanora.backend.model.auth.SignUpRequest;
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
import static com.rentanora.backend.constants.AuthConstants.*;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;

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

    public SignUpResponse register(SignUpRequest signUpRequest) {
        String email = signUpRequest.email().strip();
        String phone = signUpRequest.phone().strip();

        if (!phone.matches(PHONE_NUMBER_PATTERN)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    INVALID_PHONE_NUMBER
            );
        }

        List<AttributeType> attributes = new ArrayList<>();

        addAttribute(
                attributes,
                EMAIL_ATTRIBUTE,
                email
        );

        addAttribute(
                attributes,
                FIRST_NAME_ATTRIBUTE,
                signUpRequest.firstName()
        );

        addAttribute(
                attributes,
                MIDDLE_NAME_ATTRIBUTE,
                signUpRequest.middleName()
        );

        addAttribute(
                attributes,
                LAST_NAME_ATTRIBUTE,
                signUpRequest.lastName()
        );

        addAttribute(
                attributes,
                PHONE_ATTRIBUTE,
                phone
        );

        addAttribute(
                attributes,
                CITY_ATTRIBUTE,
                signUpRequest.city()
        );

        addAttribute(
                attributes,
                STATE_ATTRIBUTE,
                signUpRequest.state()
        );

        addAttribute(
                attributes,
                COUNTRY_ATTRIBUTE,
                signUpRequest.country()
        );

        addAttribute(
                attributes,
                PINCODE_ATTRIBUTE,
                signUpRequest.pincode()
        );

        var request =
                software.amazon.awssdk.services.cognitoidentityprovider
                        .model.SignUpRequest.builder()
                        .clientId(clientId)
                        .username(email)
                        .password(signUpRequest.password())
                        .secretHash(CognitoSecretHash.calculate(
                                email,
                                clientId,
                                clientSecret
                        ))
                        .userAttributes(attributes)
                        .build();

        return cognitoClient.signUp(request);
    }

    private static void addAttribute(
            List<AttributeType> attributes,
            String name,
            String value) {

        if (value == null || value.isBlank()) {
            return;
        }

        attributes.add(
                AttributeType.builder()
                        .name(name)
                        .value(value.strip())
                        .build()
        );
    }
}