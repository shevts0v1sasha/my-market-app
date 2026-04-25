package ru.yandex.marketapp.order.infrastructure.api.dto;

import java.util.List;

public record OrderDto(long id, List<OrderItemDto> items, long totalSum) {
}
