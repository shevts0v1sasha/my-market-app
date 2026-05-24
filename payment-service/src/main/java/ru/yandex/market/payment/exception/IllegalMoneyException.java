package ru.yandex.market.payment.exception;

public class IllegalMoneyException extends RuntimeException {

    private final MoneyExceptionCode code;

    public IllegalMoneyException(String message, MoneyExceptionCode code) {
        super(message);
        this.code = code;
    }

    public IllegalMoneyException(String message, Throwable cause, MoneyExceptionCode code) {
        super(message, cause);
        this.code = code;
    }
}
