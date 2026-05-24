package ru.yandex.market.payment.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.domain.Money;
import ru.yandex.market.payment.domain.Payment;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<UUID, Payment> paymentsById = new ConcurrentHashMap<>();
    private final Map<Long, Payment> paymentsByOrderId = new ConcurrentHashMap<>();

    @Override
    public Mono<Payment> findByOrderId(long orderId) {
        return Optional.ofNullable(paymentsByOrderId.get(orderId))
                .map(Mono::just)
                .orElseGet(Mono::empty);
    }

    @Override
    public Flux<Payment> findAll() {
        return Flux.fromIterable(paymentsById.values().stream().toList());
    }

    @Override
    public Mono<Payment> save(Long orderId, Money money) {
        Payment payment = new Payment(money, orderId);
        paymentsById.put(payment.getId(), payment);
        paymentsByOrderId.put(payment.getOrderId(), payment);
        return Mono.just(payment);
    }
}
