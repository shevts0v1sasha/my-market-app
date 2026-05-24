package ru.yandex.market.payment.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Payment {
    private final UUID id;
    private final Long orderId;
    private Instant createdAt;
    private final Money money;

    public Payment(Money money, Long orderId) {
        if (money == null) {
            throw new IllegalArgumentException("Money must not be null");
        }
        if (orderId == null) {
            throw new IllegalArgumentException("OrderId must not be null");
        }
        id = UUID.randomUUID();
        this.money = money;
        this.orderId = orderId;
    }
}
