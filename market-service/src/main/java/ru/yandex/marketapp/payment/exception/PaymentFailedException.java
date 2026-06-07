package ru.yandex.marketapp.payment.exception;

public class PaymentFailedException extends PaymentException {

    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause, "failed");
    }
}
