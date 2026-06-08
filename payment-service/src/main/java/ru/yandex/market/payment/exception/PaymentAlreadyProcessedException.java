package ru.yandex.market.payment.exception;

import lombok.Getter;

@Getter
public class PaymentAlreadyProcessedException extends RuntimeException {

    private final ExceptionCode code;

    public PaymentAlreadyProcessedException(String message, ExceptionCode code) {
        super(message);
        this.code = code;
    }

    public PaymentAlreadyProcessedException(String message, Throwable cause, ExceptionCode code) {
        super(message, cause);
        this.code = code;
    }
}
