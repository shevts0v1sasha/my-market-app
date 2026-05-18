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
import ru.yandex.marketapp.order.infrastructure.entity.OrderItemEntity;
import ru.yandex.marketapp.order.infrastructure.entity.OrderEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderR2dbcRepositoryAdapter implements OrderRepository {

    private final OrderR2dbcRepository orderR2dbcRepository;
    private final OrderItemR2dbcRepository orderItemR2dbcRepository;

    @Override
    @Transactional
    public Mono<Order> save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setTotalSum(order.totalSum());

        return orderR2dbcRepository.save(entity)
                .flatMap(savedOrder -> {
                    List<OrderItemEntity> items = order.items().stream()
                            .map(item -> toJpa(item, savedOrder.getId()))
                            .toList();
                    return orderItemR2dbcRepository.saveAll(items)
                            .collectList()
                            .map(savedItems -> toDomain(savedOrder, savedItems));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Order> findAll() {
        return orderR2dbcRepository.findAll()
                .collectList()
                .filter(orders -> !orders.isEmpty())
                .flatMapMany(orders -> {
                    List<Long> orderIds = orders.stream()
                            .map(OrderEntity::getId)
                            .toList();

                    return orderItemR2dbcRepository.findByOrderIdIn(orderIds)
                            .collectMultimap(OrderItemEntity::getOrderId)
                            .flatMapMany(itemsByOrderId ->
                                    Flux.fromIterable(orders)
                                            .map(order -> toDomain(
                                                    order,
                                                    List.copyOf(itemsByOrderId.getOrDefault(order.getId(), List.of()))
                                            ))
                            );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Order> findById(long id) {
        return orderR2dbcRepository.findById(id)
                .flatMap(order -> orderItemR2dbcRepository.findByOrderId(id)
                        .collectList()
                        .map(items -> toDomain(order, items)));
    }

    private OrderItemEntity toJpa(OrderItem item, long orderId) {
        return new OrderItemEntity(
                null,
                item.itemId(),
                item.title(),
                item.price(),
                item.count(),
                orderId
        );
    }

    private Order toDomain(OrderEntity entity, List<OrderItemEntity> orderItems) {
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
