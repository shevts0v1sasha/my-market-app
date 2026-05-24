package ru.yandex.market.payment.domain;

import ru.yandex.market.payment.exception.IllegalMoneyException;
import ru.yandex.market.payment.exception.MoneyExceptionCode;

public record Money(long amount) {

    public Money {
        if (amount <= 0) {
            throw new IllegalMoneyException("Amount should be greater then 0",
                    MoneyExceptionCode.INSUFFICIENT);
        }
    }
}
