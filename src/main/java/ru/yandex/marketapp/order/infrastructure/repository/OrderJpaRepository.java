package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderJpaEntity;

public interface OrderJpaRepository extends ReactiveCrudRepository<OrderJpaEntity, Long> {
}
