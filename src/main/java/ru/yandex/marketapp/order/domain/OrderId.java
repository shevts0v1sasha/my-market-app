package ru.yandex.marketapp.order.domain;

public record OrderId(long id) {
    public OrderId {
        if (id < 1) {
            throw new IllegalArgumentException("Order id must be positive");
        }
    }
}
