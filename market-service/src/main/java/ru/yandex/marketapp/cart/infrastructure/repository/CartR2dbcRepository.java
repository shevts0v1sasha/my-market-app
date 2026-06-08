package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.marketapp.cart.infrastructure.entity.CartEntity;

public interface CartR2dbcRepository extends ReactiveCrudRepository<CartEntity, Long> {
}
