package ru.yandex.marketapp.order.domain;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {

    Mono<Order> save(Order order, long userId);

    Flux<Order> findAllByUserId(long userId);

    Mono<Order> findByIdAndUserId(long id, long userId);

    Mono<Void> deleteById(long id);
}
