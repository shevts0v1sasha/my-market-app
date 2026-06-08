package ru.yandex.marketapp.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.client.api.PaymentsApi;
import ru.yandex.market.payment.client.model.BalanceResponse;
import ru.yandex.market.payment.client.model.PaymentRequest;
import ru.yandex.marketapp.payment.exception.PaymentFailedException;
import ru.yandex.marketapp.payment.exception.PaymentInsufficientFundsException;
import ru.yandex.marketapp.payment.exception.PaymentServiceUnavailableException;

@Service
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentsApi paymentsApi;

    public Mono<Long> getBalanceKopecks(long userId) {
        return paymentsApi.getBalance(userId)
                .map(BalanceResponse::getBalance)
                .onErrorMap(this::mapError);
    }

    public Mono<Void> processPayment(long orderId, long amountKopecks, long userId) {
        PaymentRequest request = new PaymentRequest()
                .orderId(orderId)
                .amount(amountKopecks)
                .userId(userId);

        return paymentsApi.processPayment(request)
                .then()
                .onErrorMap(this::mapError);
    }

    private Throwable mapError(Throwable error) {
        if (error instanceof WebClientResponseException responseException) {
            int status = responseException.getStatusCode().value();
            if (status == 409) {
                return new PaymentInsufficientFundsException("Not enough funds to process payment", error);
            }
            if (status == 401 || status == 403) {
                return new PaymentServiceUnavailableException("Payment service rejected authorization", error);
            }
            return new PaymentFailedException("Payment request failed", error);
        }
        return new PaymentServiceUnavailableException("Payment service is unavailable", error);
    }
}
