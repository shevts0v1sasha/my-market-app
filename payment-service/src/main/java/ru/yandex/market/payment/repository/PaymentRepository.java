package ru.yandex.market.payment.repository;

import ru.yandex.market.payment.domain.Money;
import ru.yandex.market.payment.domain.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Optional<Payment> findByOrderId(long orderId);
    List<Payment> findAll();
    Payment save(Long orderId, Money money);

}
