package com.rentanora.backend.config;

import com.rentanora.backend.model.verification.VerificationEmailProperties;
import com.rentanora.backend.model.verification.VerificationProperties;
import com.rentanora.backend.model.verification.VerificationStorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import java.time.Clock;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
        VerificationProperties.class,
        VerificationStorageProperties.class,
        VerificationEmailProperties.class
})
public class VerificationConfig {

    @Bean(destroyMethod = "close")
    public DynamoDbClient verificationDynamoDbClient(
            VerificationStorageProperties properties) {

        return DynamoDbClient.builder()
                .region(Region.of(properties.region()))
                .build();
    }

    @Bean(destroyMethod = "close")
    public SecretsManagerClient verificationSecretsManagerClient(
            VerificationStorageProperties properties) {

        return SecretsManagerClient.builder()
                .region(Region.of(properties.region()))
                .build();
    }

    @Bean(destroyMethod = "close")
    public SesV2Client verificationSesClient(
            VerificationStorageProperties properties) {

        return SesV2Client.builder()
                .region(Region.of(properties.region()))
                .build();
    }

    @Bean
    public Clock verificationClock() {
        return Clock.systemUTC();
    }
}