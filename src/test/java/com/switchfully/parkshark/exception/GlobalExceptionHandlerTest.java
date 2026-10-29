package com.switchfully.parkshark.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void givenUnexpectedException_thenReturns500WithoutInternalDetails() {
        ProblemDetail problem = handler.handleOtherExceptions(new IllegalStateException("connection to db-host:5432 refused"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getDetail()).isEqualTo(GlobalExceptionHandler.UNEXPECTED_ERROR_MESSAGE);
    }

    @Test
    void givenSpringErrorThatCarriesItsOwnStatus_thenKeepsThatStatus() {
        ProblemDetail problem = handler.handleOtherExceptions(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}