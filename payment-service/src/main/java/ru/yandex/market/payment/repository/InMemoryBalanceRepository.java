package ru.yandex.market.payment.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.domain.Money;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class InMemoryBalanceRepository implements BalanceRepository {

    private static final long DEFAULT_BALANCE = 500_000L;

    private final Map<Long, AtomicReference<Money>> balances = new ConcurrentHashMap<>();

    @Override
    public Mono<Long> getBalance(long userId) {
        return Mono.just(balanceFor(userId).get().amount());
    }

    @Override
    public Mono<Long> decreaseBalance(long userId, long money) {
        return Mono.fromCallable(() ->
                balanceFor(userId).updateAndGet(current -> new Money(current.amount() - money)).amount());
    }

    @Override
    public void reset() {
        balances.clear();
    }

    private AtomicReference<Money> balanceFor(long userId) {
        return balances.computeIfAbsent(userId, id -> new AtomicReference<>(new Money(DEFAULT_BALANCE)));
    }
}
