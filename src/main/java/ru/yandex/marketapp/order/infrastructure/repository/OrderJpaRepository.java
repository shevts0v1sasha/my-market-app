package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderJpaEntity;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {
}
