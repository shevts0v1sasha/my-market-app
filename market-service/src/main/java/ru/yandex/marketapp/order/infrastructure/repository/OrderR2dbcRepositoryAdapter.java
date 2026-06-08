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
import ru.yandex.marketapp.order.infrastructure.entity.OrderEntity;
import ru.yandex.marketapp.order.infrastructure.entity.OrderItemEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderR2dbcRepositoryAdapter implements OrderRepository {

    private final OrderR2dbcRepository orderR2dbcRepository;
    private final OrderItemR2dbcRepository orderItemR2dbcRepository;

    @Override
    @Transactional
    public Mono<Order> save(Order order, long userId) {
        OrderEntity entity = new OrderEntity(null, order.totalSum(), userId);

        return orderR2dbcRepository.save(entity)
                .flatMap(savedOrder -> {
                    List<OrderItemEntity> items = order.items().stream()
                            .map(item -> toEntity(item, savedOrder.getId()))
                            .toList();
                    return orderItemR2dbcRepository.saveAll(items)
                            .collectList()
                            .map(savedItems -> toDomain(savedOrder, savedItems));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Order> findAllByUserId(long userId) {
        return orderR2dbcRepository.findAllByUserId(userId)
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
    public Mono<Order> findByIdAndUserId(long id, long userId) {
        return orderR2dbcRepository.findByIdAndUserId(id, userId)
                .flatMap(order -> orderItemR2dbcRepository.findByOrderId(id)
                        .collectList()
                        .map(items -> toDomain(order, items)));
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return orderR2dbcRepository.deleteById(id);
    }

    private OrderItemEntity toEntity(OrderItem item, long orderId) {
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
