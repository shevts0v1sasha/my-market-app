package ru.yandex.marketapp.order.domain;

public record OrderItem(long itemId, String title, long price, int count) {
}
