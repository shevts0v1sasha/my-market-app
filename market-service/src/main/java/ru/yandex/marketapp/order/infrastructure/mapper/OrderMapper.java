package ru.yandex.marketapp.order.infrastructure.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.marketapp.order.domain.Order;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderDto;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderItemDto;

@Component
public class OrderMapper {

    public OrderDto map(Order order) {
        return new OrderDto(
                order.id().id(),
                order.items().stream()
                        .map(item -> new OrderItemDto(item.itemId(), item.title(), item.price(), item.count()))
                        .toList(),
                order.totalSum()
        );
    }
}
