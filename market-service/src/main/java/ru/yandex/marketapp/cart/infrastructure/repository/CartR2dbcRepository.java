package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.infrastructure.entity.CartEntity;

public interface CartR2dbcRepository extends ReactiveCrudRepository<CartEntity, Long> {

    Mono<CartEntity> findByUserId(Long userId);
}
