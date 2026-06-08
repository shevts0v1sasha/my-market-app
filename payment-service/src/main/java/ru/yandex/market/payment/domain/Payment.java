package ru.yandex.market.payment.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Getter
public class Payment {
    private static final AtomicLong ID_SEQUENCE = new AtomicLong(1);

    private final Long id;
    private final Long orderId;
    private final Instant createdAt;
    private final Money money;

    public Payment(Money money, Long orderId) {
        if (money == null) {
            throw new IllegalArgumentException("Money must not be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("OrderId must not be null");
        }
        id = ID_SEQUENCE.getAndIncrement();
        this.money = money;
        this.orderId = orderId;
        this.createdAt = Instant.now();
    }
}
