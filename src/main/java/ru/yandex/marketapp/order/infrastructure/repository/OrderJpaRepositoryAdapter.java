package ru.yandex.marketapp.order.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.order.domain.Order;
import ru.yandex.marketapp.order.domain.OrderId;
import ru.yandex.marketapp.order.domain.OrderItem;
import ru.yandex.marketapp.order.domain.OrderRepository;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderItemJpaEntity;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderJpaEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderJpaRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    @Transactional
    public Mono<Order> save(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setTotalSum(order.totalSum());

        return orderJpaRepository.save(entity)
                .flatMap(savedOrder -> {
                    List<OrderItemJpaEntity> items = order.items().stream()
                            .map(item -> toJpa(item, savedOrder.getId()))
                            .toList();
                    return orderItemJpaRepository.saveAll(items)
                            .collectList()
                            .map(savedItems -> toDomain(savedOrder, savedItems));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Order> findAll() {
        return orderJpaRepository.findAll()
                .collectList()
                .flatMapMany(orders -> {
                    if (orders.isEmpty()) {
                        return Flux.empty();
                    }
                    List<Long> orderIds = orders.stream()
                            .map(OrderJpaEntity::getId)
                            .toList();
                    return orderItemJpaRepository.findByOrderIdIn(orderIds)
                            .collectMultimap(OrderItemJpaEntity::getOrderId)
                            .flatMapMany(itemsByOrderId -> Flux.fromIterable(orders)
                                    .map(order -> toDomain(
                                            order,
                                            List.copyOf(itemsByOrderId.getOrDefault(order.getId(), List.of()))
                                    )));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Order> findById(long id) {
        return orderJpaRepository.findById(id)
                .flatMap(order -> orderItemJpaRepository.findByOrderId(id)
                        .collectList()
                        .map(items -> toDomain(order, items)));
    }

    private OrderItemJpaEntity toJpa(OrderItem item, long orderId) {
        return new OrderItemJpaEntity(
                null,
                item.itemId(),
                item.title(),
                item.price(),
                item.count(),
                orderId
        );
    }

    private Order toDomain(OrderJpaEntity entity, List<OrderItemJpaEntity> orderItems) {
        List<OrderItem> items = orderItems.stream()
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
