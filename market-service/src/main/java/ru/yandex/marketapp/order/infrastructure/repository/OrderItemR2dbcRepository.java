package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.marketapp.order.infrastructure.entity.OrderItemEntity;

import java.util.List;

public interface OrderItemR2dbcRepository extends ReactiveCrudRepository<OrderItemEntity, Long> {

    Flux<OrderItemEntity> findByOrderId(Long orderId);

    Flux<OrderItemEntity> findByOrderIdIn(List<Long> orderIds);
}
