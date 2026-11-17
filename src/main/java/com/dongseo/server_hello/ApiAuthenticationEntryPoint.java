package com.dongseo.server_hello;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

// security filters run BEFORE any controller so @RestControllerAdvice never sees their errors
// spring calls commence() when a request needs login and is missing one
@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver resolver;

    public ApiAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) {
        resolver.resolveException(req, res, null, e);
    }
}
