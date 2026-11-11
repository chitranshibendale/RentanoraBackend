package com.rentanora.backend.auth;

import static com.rentanora.backend.constants.AuthConstants.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                INVALID_SIGN_UP_DETAILS
        );
    }

    @ExceptionHandler(CognitoIdentityProviderException.class)
    public ProblemDetail handleCognito(
            CognitoIdentityProviderException exception) {

        HttpStatus status;
        String detail;

        if (exception instanceof UsernameExistsException) {
            status = HttpStatus.CONFLICT;
            detail = ACCOUNT_ALREADY_EXISTS;

        } else if (exception instanceof InvalidPasswordException) {
            status = HttpStatus.BAD_REQUEST;
            detail = PASSWORD_REQUIREMENTS_NOT_MET;

        } else if (exception instanceof CodeMismatchException) {
            status = HttpStatus.BAD_REQUEST;
            detail = INVALID_CONFIRMATION_CODE;

        } else if (exception instanceof ExpiredCodeException) {
            status = HttpStatus.BAD_REQUEST;
            detail = EXPIRED_CONFIRMATION_CODE;

        } else if (
                exception instanceof TooManyRequestsException
                        || exception instanceof TooManyFailedAttemptsException
                        || exception instanceof LimitExceededException) {

            status = HttpStatus.TOO_MANY_REQUESTS;
            detail = TOO_MANY_AUTH_REQUESTS;

        } else if (
                exception instanceof InvalidParameterException
                        || exception instanceof NotAuthorizedException
                        || exception instanceof UserNotFoundException
                        || exception instanceof AliasExistsException) {

            status = HttpStatus.BAD_REQUEST;
            detail = INVALID_SIGN_UP_DETAILS;

        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            detail = AUTH_SERVICE_UNAVAILABLE;
        }

        return ProblemDetail.forStatusAndDetail(status, detail);
    }

    @ExceptionHandler(SdkClientException.class)
    public ProblemDetail handleConnectionFailure(
            SdkClientException exception) {

        return ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                AUTH_SERVICE_UNAVAILABLE
        );
    }
}