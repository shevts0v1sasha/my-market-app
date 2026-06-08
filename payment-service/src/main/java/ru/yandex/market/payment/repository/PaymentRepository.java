package ru.yandex.market.payment.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.domain.Money;
import ru.yandex.market.payment.domain.Payment;

public interface PaymentRepository {

    Mono<Payment> findByOrderId(long orderId);
    Flux<Payment> findAll();
    Mono<Payment> save(Long orderId, Money money);

}
