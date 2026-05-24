package ru.yandex.market.payment.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.api.model.BalanceResponse;
import ru.yandex.market.payment.api.model.PaymentRequest;
import ru.yandex.market.payment.api.model.PaymentResponse;

public class PaymentV1RestController implements PaymentsApi {


    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(ServerWebExchange exchange) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> processPayment(Mono<PaymentRequest> paymentRequest, ServerWebExchange exchange) {
        return null;
    }
}
