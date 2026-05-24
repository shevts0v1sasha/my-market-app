package ru.yandex.market.payment.application.usecase;

import ru.yandex.market.payment.domain.Money;

public record ProcessPaymentRequest(Long orderId,
                                    Money money) {
}
