package ru.yandex.market.payment.repository;

import org.springframework.stereotype.Repository;
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
    public Optional<Payment> findByOrderId(long orderId) {
        return Optional.ofNullable(paymentsByOrderId.get(orderId));
    }

    @Override
    public List<Payment> findAll() {
        return paymentsById.values().stream().toList();
    }

    @Override
    public Payment save(Long orderId, Money money) {
        Payment payment = new Payment(money, orderId);
        paymentsById.put(payment.getId(), payment);
        paymentsByOrderId.put(payment.getOrderId(), payment);
        return payment;
    }
}
