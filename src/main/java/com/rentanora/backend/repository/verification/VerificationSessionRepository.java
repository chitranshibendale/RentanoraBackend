package com.rentanora.backend.repository.verification;

import com.rentanora.backend.mapper.verification.VerificationSessionMapper;
import com.rentanora.backend.model.verification.VerificationSession;
import com.rentanora.backend.model.verification.VerificationStorageProperties;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;
import java.util.Optional;

import static com.rentanora.backend.constants.VerificationStorageConstants.CREATE_RECORD_CONDITION;
import static com.rentanora.backend.constants.VerificationStorageConstants.ID;
import static com.rentanora.backend.constants.VerificationStorageConstants.ID_ALIAS;

@Repository
public class VerificationSessionRepository {

    private final DynamoDbClient dynamoDbClient;
    private final VerificationStorageProperties properties;
    private final VerificationSessionMapper mapper;

    public VerificationSessionRepository(
            DynamoDbClient dynamoDbClient,
            VerificationStorageProperties properties,
            VerificationSessionMapper mapper) {

        this.dynamoDbClient = dynamoDbClient;
        this.properties = properties;
        this.mapper = mapper;
    }

    public Optional<VerificationSession> findById(String id) {
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

    public boolean createIfAbsent(VerificationSession session) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(properties.tableName())
                .item(mapper.toItem(session))
                .conditionExpression(CREATE_RECORD_CONDITION)
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