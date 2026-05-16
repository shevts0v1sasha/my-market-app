package ru.yandex.marketapp.order.infrastructure.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.marketapp.order.infrastructure.jpa.OrderJpaEntity;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, Long> {

    @EntityGraph(attributePaths = "items")
    List<OrderJpaEntity> findAll();

    @EntityGraph(attributePaths = "items")
    Optional<OrderJpaEntity> findById(Long id);
}
