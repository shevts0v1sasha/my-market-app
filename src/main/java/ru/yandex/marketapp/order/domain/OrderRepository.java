package ru.yandex.marketapp.order.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {
    Mono<Order> save(Order order);

    Flux<Order> findAll();

    Mono<Order> findById(long id);
}
