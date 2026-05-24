package ru.yandex.market.payment.exception;

public class IllegalMoneyException extends RuntimeException {

    private final ExceptionCode code;

    public IllegalMoneyException(String message, ExceptionCode code) {
        super(message);
        this.code = code;
    }

    public IllegalMoneyException(String message, Throwable cause, ExceptionCode code) {
        super(message, cause);
        this.code = code;
    }
}
