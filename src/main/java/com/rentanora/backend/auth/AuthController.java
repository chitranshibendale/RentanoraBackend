package com.rentanora.backend.auth;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class AuthController {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            AuthService authService,
            SecurityContextRepository securityContextRepository) {

        this.authService = authService;
        this.securityContextRepository = securityContextRepository;
    }

    @GetMapping("/api/auth/me")
    public UserResponse currentUser(Principal principal) {
        return new UserResponse(principal.getName());
    }

    public record UserResponse(String userId) {}

    @PostMapping("/api/auth/login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        String userId = authService.authenticateUser(loginRequest);

        request.getSession();
        request.changeSessionId();

        var authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        userId,
                        null,
                        List.of()
                );

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(
                context,
                request,
                response
        );

        new HttpSessionCsrfTokenRepository().saveToken(
                null,
                request,
                response
        );

        response.setHeader("Cache-Control", "no-store");
    }
}