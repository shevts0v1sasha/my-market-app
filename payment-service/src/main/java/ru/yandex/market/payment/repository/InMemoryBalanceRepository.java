package ru.yandex.market.payment.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.domain.Money;

import java.util.concurrent.atomic.AtomicReference;

@Repository
public class InMemoryBalanceRepository implements BalanceRepository {

    private final AtomicReference<Money> balance = new AtomicReference<>(new Money(500_000));

    @Override
    public Mono<Long> getBalance() {
        return Mono.just(balance.get().amount());
    }

    @Override
    public Mono<Long> increaseBalance(long money) {
        return Mono.just(balance.updateAndGet(m -> new Money(m.amount() + money)).amount());
    }

    @Override
    public Mono<Long> decreaseBalance(long money) {
        return Mono.just(balance.updateAndGet(m -> new Money(m.amount() - money)).amount());
    }
}
