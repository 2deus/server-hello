package com.dongseo.server_hello;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class DemoExceptionHandler {
    private static final int MYSQL_CHECK_VIOLATED = 3819;

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccessException(DataAccessException e) {
        if (e.getMostSpecificCause() instanceof SQLException cause) {
                //CHECK constraint failed
                if (cause.getErrorCode() == MYSQL_CHECK_VIOLATED)
                    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, cause.getMessage());
                //SQLState class 23 == integrity violation (fk_reservation_demo) -> conflict with existing data
                if (cause.getSQLState() != null && cause.getSQLState().startsWith("23"))
                    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, cause.getMessage());
        }
        //server problem
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE ERROR!!!!!!!!!!!!");
    }
}
