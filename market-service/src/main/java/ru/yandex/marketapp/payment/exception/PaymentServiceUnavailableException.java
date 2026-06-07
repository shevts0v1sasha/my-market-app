package ru.yandex.marketapp.payment.exception;

public class PaymentServiceUnavailableException extends PaymentException {

    public PaymentServiceUnavailableException(String message, Throwable cause) {
        super(message, cause, "unavailable");
    }
}
