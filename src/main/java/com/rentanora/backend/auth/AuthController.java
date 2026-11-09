package com.rentanora.backend.auth;

import static com.rentanora.backend.constants.AuthConstants.*;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(LOGIN_PATH)
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        var result = authService.authenticate(loginRequest)
                .authenticationResult();

        if (result == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    ADDITIONAL_AUTH_REQUIRED
            );
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new LoginResponse(
                        result.accessToken(),
                        result.expiresIn()
                ));
    }

    @GetMapping(CURRENT_USER_PATH)
    public UserResponse currentUser(Principal principal) {
        return new UserResponse(principal.getName());
    }

    public record UserResponse(String userId) {
    }

    public record LoginResponse(String accessToken, Integer expiresIn) {

        @Override
        public String toString() {
            return REDACTED_LOGIN_RESPONSE;
        }
    }
}