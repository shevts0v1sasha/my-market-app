package ru.yandex.marketapp.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.marketapp.common.application.BusinessRuleException;
import ru.yandex.marketapp.common.application.NotFoundException;

import java.util.List;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ValidationExceptionDto> handle(WebExchangeBindException e) {
        List<String> errors = e.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return "%s: %s".formatted(
                                fieldError.getField(),
                                fieldError.getDefaultMessage()
                        );
                    }

                    return "%s: %s".formatted(
                            error.getObjectName(),
                            error.getDefaultMessage()
                    );
                })
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationExceptionDto("Validation exception", errors));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ValidationExceptionDto> handle(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ValidationExceptionDto(e.getMessage(), List.of(e.getMessage())));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ValidationExceptionDto> handle(BusinessRuleException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationExceptionDto(e.getMessage(), List.of(e.getMessage())));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ValidationExceptionDto> handle(ResponseStatusException e) {
        String message = e.getReason() == null ? e.getStatusCode().toString() : e.getReason();
        return ResponseEntity.status(e.getStatusCode())
                .body(new ValidationExceptionDto(message, List.of(message)));
    }
}
