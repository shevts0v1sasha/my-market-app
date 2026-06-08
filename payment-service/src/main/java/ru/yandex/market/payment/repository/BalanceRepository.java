package ru.yandex.market.payment.repository;

import reactor.core.publisher.Mono;

public interface BalanceRepository {

    Mono<Long> getBalance(long userId);

    Mono<Long> decreaseBalance(long userId, long money);

    void reset();
}
