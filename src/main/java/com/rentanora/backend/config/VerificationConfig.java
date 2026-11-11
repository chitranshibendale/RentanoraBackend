package com.rentanora.backend.config;

import com.rentanora.backend.model.verification.VerificationProperties;
import com.rentanora.backend.model.verification.VerificationStorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        VerificationProperties.class,
        VerificationStorageProperties.class
})
public class VerificationConfig {

    @Bean(destroyMethod = "close")
    public DynamoDbClient verificationDynamoDbClient(
            VerificationStorageProperties properties) {

        return DynamoDbClient.builder()
                .region(Region.of(properties.region()))
                .build();
    }
}