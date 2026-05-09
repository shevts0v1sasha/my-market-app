package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.marketapp.cart.infrastructure.jpa.CartJpaEntity;

import java.util.Optional;

public interface CartJpaRepository extends JpaRepository<CartJpaEntity, Long> {

    @EntityGraph(attributePaths = "items")
    Optional<CartJpaEntity> findById(Long id);
}
