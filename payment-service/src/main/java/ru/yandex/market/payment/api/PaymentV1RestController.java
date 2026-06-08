package ru.yandex.market.payment.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.api.model.BalanceResponse;
import ru.yandex.market.payment.api.model.PaymentRequest;
import ru.yandex.market.payment.api.model.PaymentResponse;
import ru.yandex.market.payment.api.model.PaymentStatus;
import ru.yandex.market.payment.application.service.query.BalanceQueryService;
import ru.yandex.market.payment.application.usecase.ProcessPaymentRequest;
import ru.yandex.market.payment.application.usecase.ProcessPaymentUseCase;
import ru.yandex.market.payment.domain.Money;

@RestController
@RequiredArgsConstructor
public class PaymentV1RestController implements PaymentsApi {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final BalanceQueryService balanceQueryService;

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(Long userId, ServerWebExchange exchange) {
        return balanceQueryService.getBalance(userId)
                .map(money -> ResponseEntity.ok(new BalanceResponse(money.amount())));
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> processPayment(Mono<PaymentRequest> paymentRequest,
                                                                ServerWebExchange exchange) {
        return paymentRequest
                .flatMap(r -> processPaymentUseCase.handle(
                        new ProcessPaymentRequest(
                                r.getOrderId(),
                                r.getUserId(),
                                new Money(r.getAmount()))
                ))
                .map(p -> ResponseEntity.ok(
                        new PaymentResponse(p.getId(),
                                p.getOrderId(),
                                p.getMoney().amount(),
                                PaymentStatus.SUCCESS))
                );
    }
}
