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

    public Mono<Long> getBalanceKopecks() {
        return paymentsApi.getBalance()
                .map(BalanceResponse::getBalance)
                .onErrorMap(this::mapError);
    }

    public Mono<Void> processPayment(long orderId, long amountKopecks) {
        PaymentRequest request = new PaymentRequest()
                .orderId(orderId)
                .amount(amountKopecks);

        return paymentsApi.processPayment(request)
                .then()
                .onErrorMap(this::mapError);
    }

    private Throwable mapError(Throwable error) {
        if (error instanceof WebClientResponseException responseException) {
            if (responseException.getStatusCode().value() == 409) {
                return new PaymentInsufficientFundsException("Not enough funds to process payment", error);
            }
            return new PaymentFailedException("Payment request failed", error);
        }
        return new PaymentServiceUnavailableException("Payment service is unavailable", error);
    }
}
