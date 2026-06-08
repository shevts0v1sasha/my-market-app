package ru.yandex.market.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.market.payment.api.model.ErrorResponse;

@RestControllerAdvice
public class AdviceControllerHandler {

    @ExceptionHandler(PaymentAlreadyProcessedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentAlreadyProcessedException(PaymentAlreadyProcessedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse()
                        .code(e.getCode().name())
                        .message(e.getMessage()));
    }

    @ExceptionHandler(IllegalMoneyException.class)
    public ResponseEntity<ErrorResponse> handleIllegalMoneyException(IllegalMoneyException e) {
        HttpStatus status = switch (e.getCode()) {
            case INSUFFICIENT_FUNDS -> HttpStatus.CONFLICT;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status)
                .body(new ErrorResponse()
                        .code(e.getCode().name())
                        .message(e.getMessage()));
    }
}
