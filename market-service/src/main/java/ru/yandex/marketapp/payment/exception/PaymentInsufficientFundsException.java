package ru.yandex.marketapp.payment.exception;

public class PaymentInsufficientFundsException extends RuntimeException {

    public PaymentInsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}
