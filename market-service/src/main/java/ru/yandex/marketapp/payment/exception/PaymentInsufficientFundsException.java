package ru.yandex.marketapp.payment.exception;

public class PaymentInsufficientFundsException extends PaymentException {

    public PaymentInsufficientFundsException(String message, Throwable cause) {
        super(message, cause, "insufficient");
    }
}
