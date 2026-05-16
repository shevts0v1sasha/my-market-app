package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.infrastructure.jpa.CartItemJpaEntity;

public interface CartItemJpaRepository extends ReactiveCrudRepository<CartItemJpaEntity, Long> {

    Flux<CartItemJpaEntity> findByCartId(Long cartId);

    Mono<Void> deleteByCartId(Long cartId);
}
