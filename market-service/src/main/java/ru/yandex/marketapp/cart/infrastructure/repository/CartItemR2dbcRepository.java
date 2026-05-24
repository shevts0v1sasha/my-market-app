package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.infrastructure.entity.CartItemEntity;

public interface CartItemR2dbcRepository extends ReactiveCrudRepository<CartItemEntity, Long> {

    Flux<CartItemEntity> findByCartId(Long cartId);

    Mono<Void> deleteByCartId(Long cartId);
}
