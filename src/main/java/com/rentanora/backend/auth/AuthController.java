package com.rentanora.backend.auth;

import static com.rentanora.backend.constants.AuthConstants.*;

import java.security.Principal;

import com.rentanora.backend.model.auth.LoginResponse;
import com.rentanora.backend.model.auth.RegistrationResponse;
import com.rentanora.backend.model.auth.SignUpRequest;
import com.rentanora.backend.model.auth.UserResponse;
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

    @PostMapping(SIGN_UP_PATH)
    public ResponseEntity<RegistrationResponse> signUp(
            @Valid @RequestBody SignUpRequest signUpRequest) {

        var result = authService.register(signUpRequest);
        var delivery = result.codeDeliveryDetails();

        return ResponseEntity.status(HttpStatus.CREATED)
                .cacheControl(CacheControl.noStore())
                .body(new RegistrationResponse(
                        Boolean.TRUE.equals(result.userConfirmed()),
                        delivery == null ? null : delivery.destination()
                ));
    }

    @GetMapping(CURRENT_USER_PATH)
    public UserResponse currentUser(Principal principal) {
        return new UserResponse(principal.getName());
    }
}