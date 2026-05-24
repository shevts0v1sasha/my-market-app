package ru.yandex.marketapp.order.domain;

import java.util.List;

public record Order(OrderId id, List<OrderItem> items, long totalSum) {

    public Order {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        if (totalSum < 1) {
            throw new IllegalArgumentException("Order total sum must be positive");
        }
    }

    public static Order create(List<OrderItem> items) {
        long totalSum = items.stream()
                .mapToLong(i -> i.price() * i.count())
                .sum();
        return new Order(null, items, totalSum);
    }
}
