package ru.yandex.marketapp.order.infrastructure.api.dto;

public record OrderItemDto(long id, String title, long price, int count) {
}
