package ru.yandex.market.payment.repository;

public interface BalanceRepository {
    long getBalance();
    long increaseBalance(long money);
    long decreaseBalance(long money);
}
