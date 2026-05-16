package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderItemJpaEntity;

import java.util.List;

public interface OrderItemJpaRepository extends ReactiveCrudRepository<OrderItemJpaEntity, Long> {

    Flux<OrderItemJpaEntity> findByOrderId(Long orderId);

    Flux<OrderItemJpaEntity> findByOrderIdIn(List<Long> orderIds);
}
