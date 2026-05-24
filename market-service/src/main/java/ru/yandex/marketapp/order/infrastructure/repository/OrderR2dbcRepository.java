package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.marketapp.order.infrastructure.entity.OrderEntity;

public interface OrderR2dbcRepository extends ReactiveCrudRepository<OrderEntity, Long> {
}
