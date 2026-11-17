package com.dongseo.server_hello.Exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private ResponseEntity<ApiError> respond(
            HttpStatus status, String message,
            HttpServletRequest req,
            List<ApiError.FieldMessage> fieldErrors
            ) {
        ApiError body = ApiError.of(status, message, req.getRequestURI(), fieldErrors);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalidFields(MethodArgumentNotValidException e, HttpServletRequest req) {
        List<ApiError.FieldMessage> fields = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiError.FieldMessage(
                        f.getField(), f.getDefaultMessage()
                )).toList();
        return respond(HttpStatus.BAD_REQUEST, "request has invalid fields", req, fields);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> badJSON(MethodArgumentTypeMismatchException e, HttpServletRequest req) {
        return respond(HttpStatus.BAD_REQUEST, "'" + e.getName() +"' has wrong type", req, List.of());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> badJSON(HttpMessageNotReadableException e, HttpServletRequest req) {
        return respond(HttpStatus.BAD_REQUEST, "request body is missing or is not valid JSON", req, List.of());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFound(NotFoundException e, HttpServletRequest req) {
        return respond(HttpStatus.NOT_FOUND, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> conflict(ConflictException e, HttpServletRequest req) {
        return respond(HttpStatus.CONFLICT, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> illegalArgument(IllegalArgumentException e, HttpServletRequest req) {
        return respond(HttpStatus.BAD_REQUEST, e.getMessage(), req, List.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> unauthorized(AuthenticationException e, HttpServletRequest req) {
        String message = "login required";
        if (e instanceof BadCredentialsException) { message = "wrong username or password"; }
        else if (e instanceof InvalidBearerTokenException) { message = "token is invalid or expired"; }
        return respond(HttpStatus.UNAUTHORIZED,
                message, req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> other(Exception e, HttpServletRequest req) {
        // spring exceptions (unknown url, wrong method, missing param, etc.)
        if (e instanceof ErrorResponse spring) {
            HttpStatus status = HttpStatus.valueOf(spring.getStatusCode().value());
            return respond(status, spring.getBody().getDetail(), req, List.of());
        }
        //real bug, panic
        log.error("Unexpected error at {} {}", req.getMethod(), req.getRequestURI(), e);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", req, List.of());
    }
}
