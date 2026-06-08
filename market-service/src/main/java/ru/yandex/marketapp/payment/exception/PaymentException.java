package ru.yandex.marketapp.payment.exception;

public abstract class PaymentException extends RuntimeException {

    private final String paymentErrorCode;

    protected PaymentException(String message, Throwable cause, String paymentErrorCode) {
        super(message, cause);
        this.paymentErrorCode = paymentErrorCode;
    }

    public String getPaymentErrorCode() {
        return paymentErrorCode;
    }
}
