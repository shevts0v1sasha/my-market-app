package ru.yandex.market.payment.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.market.payment.domain.Money;

import java.util.concurrent.atomic.AtomicReference;

@Repository
public class InMemoryBalanceRepository implements BalanceRepository {

    private final AtomicReference<Money> balance = new AtomicReference<>(new Money(500_000));

    @Override
    public long getBalance() {
        return balance.get().amount();
    }

    @Override
    public long increaseBalance(long money) {
        return balance.updateAndGet(m -> new Money(m.amount() + money)).amount();
    }

    @Override
    public long decreaseBalance(long money) {
        return balance.updateAndGet(m -> new Money(m.amount() - money)).amount();
    }
}
