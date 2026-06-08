package ru.yandex.marketapp.cart.domain;

import reactor.core.publisher.Mono;

public interface CartRepository {
    Mono<Cart> getCurrentCart();
    Mono<Cart> save(Cart cart);
}
