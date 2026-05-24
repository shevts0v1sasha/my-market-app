package ru.yandex.market.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AdviceControllerHandler {

    @ExceptionHandler(PaymentAlreadyProcessedException.class)
    public ProblemDetail handlePaymentAlreadyProcessedException(PaymentAlreadyProcessedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Payment already processed");
        problemDetail.setProperty("code", e.getCode().name());
        problemDetail.setDetail(e.getMessage());

        return problemDetail;
    }
}
