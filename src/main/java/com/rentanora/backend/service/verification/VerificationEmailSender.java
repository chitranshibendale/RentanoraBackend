package com.rentanora.backend.service.verification;

import com.rentanora.backend.model.verification.VerificationChannel;
import com.rentanora.backend.model.verification.VerificationEmailProperties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

import java.util.regex.Pattern;

import static com.rentanora.backend.constants.VerificationConstants.CODE_PATTERN;
import static com.rentanora.backend.constants.VerificationConstants.INVALID_CODE;
import static com.rentanora.backend.constants.VerificationEmailConstants.VERIFICATION_BODY;
import static com.rentanora.backend.constants.VerificationEmailConstants.VERIFICATION_SUBJECT;
import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class VerificationEmailSender {

    private static final Pattern VALID_CODE =
            Pattern.compile(CODE_PATTERN);

    private final SesV2Client sesClient;
    private final VerificationEmailProperties properties;
    private final VerificationContactNormalizer contactNormalizer;

    public VerificationEmailSender(
            SesV2Client sesClient,
            VerificationEmailProperties properties,
            VerificationContactNormalizer contactNormalizer) {

        this.sesClient = sesClient;
        this.properties = properties;
        this.contactNormalizer = contactNormalizer;
    }

    public void send(String destination, String code) {
        if (code == null || !VALID_CODE.matcher(code).matches()) {
            throw new IllegalArgumentException(INVALID_CODE);
        }

        String recipient = contactNormalizer.normalize(
                VerificationChannel.EMAIL,
                destination
        );

        SendEmailRequest request = SendEmailRequest.builder()
                .fromEmailAddress(properties.fromAddress())
                .destination(builder -> builder.toAddresses(recipient))
                .content(content -> content.simple(message -> message
                        .subject(subject -> subject
                                .charset(UTF_8.name())
                                .data(VERIFICATION_SUBJECT))
                        .body(body -> body.text(text -> text
                                .charset(UTF_8.name())
                                .data(VERIFICATION_BODY.formatted(code))))))
                .build();

        sesClient.sendEmail(request);
    }
}