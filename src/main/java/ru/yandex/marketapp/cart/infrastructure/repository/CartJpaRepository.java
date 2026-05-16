package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.marketapp.cart.infrastructure.jpa.CartJpaEntity;

public interface CartJpaRepository extends ReactiveCrudRepository<CartJpaEntity, Long> {
}
