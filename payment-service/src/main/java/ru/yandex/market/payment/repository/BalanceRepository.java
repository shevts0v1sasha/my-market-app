package ru.yandex.market.payment.repository;

import reactor.core.publisher.Mono;

public interface BalanceRepository {
    Mono<Long> getBalance();
    Mono<Long> increaseBalance(long money);
    Mono<Long> decreaseBalance(long money);
}
