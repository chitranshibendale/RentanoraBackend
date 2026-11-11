package com.rentanora.backend.model.auth;

public record RegistrationResponse(
        boolean confirmed,
        String deliveryDestination) {
}