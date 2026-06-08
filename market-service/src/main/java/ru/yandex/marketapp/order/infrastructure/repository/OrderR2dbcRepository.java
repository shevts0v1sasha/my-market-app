package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.order.infrastructure.entity.OrderEntity;

public interface OrderR2dbcRepository extends ReactiveCrudRepository<OrderEntity, Long> {

    Flux<OrderEntity> findAllByUserId(Long userId);

    Mono<OrderEntity> findByIdAndUserId(Long id, Long userId);
}
