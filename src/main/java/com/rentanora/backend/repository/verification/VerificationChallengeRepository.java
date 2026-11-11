package com.rentanora.backend.repository.verification;

import com.rentanora.backend.mapper.verification.VerificationChallengeMapper;
import com.rentanora.backend.model.verification.VerificationChallenge;
import com.rentanora.backend.model.verification.VerificationStorageProperties;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

import java.util.Map;
import java.util.Optional;

import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import static com.rentanora.backend.constants.VerificationStorageConstants.ATTEMPTS;
import static com.rentanora.backend.constants.VerificationStorageConstants.ATTEMPTS_ALIAS;
import static com.rentanora.backend.constants.VerificationStorageConstants.CHALLENGE_ID;
import static com.rentanora.backend.constants.VerificationStorageConstants.CHALLENGE_ID_ALIAS;
import static com.rentanora.backend.constants.VerificationStorageConstants.CREATE_CHALLENGE_CONDITION;
import static com.rentanora.backend.constants.VerificationStorageConstants.EXPECTED_ATTEMPTS_VALUE;
import static com.rentanora.backend.constants.VerificationStorageConstants.EXPECTED_CHALLENGE_ID_VALUE;
import static com.rentanora.backend.constants.VerificationStorageConstants.EXPECTED_STATUS_VALUE;
import static com.rentanora.backend.constants.VerificationStorageConstants.EXPIRES_AT;
import static com.rentanora.backend.constants.VerificationStorageConstants.EXPIRES_AT_ALIAS;
import static com.rentanora.backend.constants.VerificationStorageConstants.ID;
import static com.rentanora.backend.constants.VerificationStorageConstants.ID_ALIAS;
import static com.rentanora.backend.constants.VerificationStorageConstants.NOW_VALUE;
import static com.rentanora.backend.constants.VerificationStorageConstants.REPLACE_CHALLENGE_CONDITION;
import static com.rentanora.backend.constants.VerificationStorageConstants.STATUS;
import static com.rentanora.backend.constants.VerificationStorageConstants.STATUS_ALIAS;

import java.util.Objects;

@Repository
public class VerificationChallengeRepository {

    private final DynamoDbClient dynamoDbClient;
    private final VerificationStorageProperties properties;
    private final VerificationChallengeMapper mapper;

    public VerificationChallengeRepository(
            DynamoDbClient dynamoDbClient,
            VerificationStorageProperties properties,
            VerificationChallengeMapper mapper) {

        this.dynamoDbClient = dynamoDbClient;
        this.properties = properties;
        this.mapper = mapper;
    }

    public boolean replaceIfUnchanged(
            VerificationChallenge expected,
            VerificationChallenge updated,
            long now) {

        if (!Objects.equals(expected.id(), updated.id())
                || !Objects.equals(
                expected.sessionId(),
                updated.sessionId()
        )
                || expected.channel() != updated.channel()
                || expected.expiresAt() != updated.expiresAt()) {

            return false;
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(properties.tableName())
                .item(mapper.toItem(updated))
                .conditionExpression(REPLACE_CHALLENGE_CONDITION)
                .expressionAttributeNames(Map.of(
                        CHALLENGE_ID_ALIAS, CHALLENGE_ID,
                        STATUS_ALIAS, STATUS,
                        ATTEMPTS_ALIAS, ATTEMPTS,
                        EXPIRES_AT_ALIAS, EXPIRES_AT
                ))
                .expressionAttributeValues(Map.of(
                        EXPECTED_CHALLENGE_ID_VALUE,
                        AttributeValue.builder()
                                .s(expected.challengeId())
                                .build(),

                        EXPECTED_STATUS_VALUE,
                        AttributeValue.builder()
                                .s(expected.status().name())
                                .build(),

                        EXPECTED_ATTEMPTS_VALUE,
                        AttributeValue.builder()
                                .n(Integer.toString(expected.attempts()))
                                .build(),

                        NOW_VALUE,
                        AttributeValue.builder()
                                .n(Long.toString(now))
                                .build()
                ))
                .build();

        try {
            dynamoDbClient.putItem(request);
            return true;
        } catch (ConditionalCheckFailedException exception) {
            return false;
        }
    }

    public Optional<VerificationChallenge> findById(String id) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(properties.tableName())
                .key(Map.of(
                        ID,
                        AttributeValue.builder().s(id).build()
                ))
                .consistentRead(true)
                .build();

        Map<String, AttributeValue> item =
                dynamoDbClient.getItem(request).item();

        if (item.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(mapper.fromItem(item));
    }

    public boolean createIfAbsent(VerificationChallenge challenge) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(properties.tableName())
                .item(mapper.toItem(challenge))
                .conditionExpression(CREATE_CHALLENGE_CONDITION)
                .expressionAttributeNames(Map.of(ID_ALIAS, ID))
                .build();

        try {
            dynamoDbClient.putItem(request);
            return true;
        } catch (ConditionalCheckFailedException exception) {
            return false;
        }
    }
}