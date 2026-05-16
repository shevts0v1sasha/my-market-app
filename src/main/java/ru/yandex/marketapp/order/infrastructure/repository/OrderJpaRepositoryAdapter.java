package ru.yandex.marketapp.order.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.marketapp.order.domain.Order;
import ru.yandex.marketapp.order.domain.OrderId;
import ru.yandex.marketapp.order.domain.OrderItem;
import ru.yandex.marketapp.order.domain.OrderRepository;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderItemJpaEntity;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderJpaEntity;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderJpaRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setTotalSum(order.totalSum());
        List<OrderItemJpaEntity> items = order.items().stream()
                .map(this::toJpaWithoutOrder)
                .toList();
        items.forEach(item -> item.setOrder(entity));
        entity.setItems(items);

        return toDomain(orderJpaRepository.save(entity));
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(long id) {
        return orderJpaRepository.findById(id)
                .map(this::toDomain);
    }

    private OrderItemJpaEntity toJpaWithoutOrder(OrderItem item) {
        return new OrderItemJpaEntity(
                null,
                item.itemId(),
                item.title(),
                item.price(),
                item.count(),
                null
        );
    }

    private Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(item -> new OrderItem(
                        item.getItemId(),
                        item.getTitle(),
                        item.getPrice(),
                        item.getCount()
                ))
                .toList();
        return new Order(new OrderId(entity.getId()), items, entity.getTotalSum());
    }
}
