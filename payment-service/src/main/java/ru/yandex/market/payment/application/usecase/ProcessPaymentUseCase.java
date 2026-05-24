package ru.yandex.market.payment.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.domain.Money;
import ru.yandex.market.payment.domain.Payment;
import ru.yandex.market.payment.exception.IllegalMoneyException;
import ru.yandex.market.payment.exception.ExceptionCode;
import ru.yandex.market.payment.exception.PaymentAlreadyProcessedException;
import ru.yandex.market.payment.repository.BalanceRepository;
import ru.yandex.market.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final BalanceRepository balanceRepository;

    public Mono<Payment> handle(ProcessPaymentRequest request) {
        return balanceRepository.getBalance()
                .flatMap(balance -> {
                    if (balance - request.money().amount() < 0) {
                        return Mono.error(new IllegalMoneyException(
                                "Not enough money for purchase order with id=%d"
                                        .formatted(request.orderId()),
                                ExceptionCode.INSUFFICIENT_MONEY
                        ));
                    }

                    return paymentRepository.findByOrderId(request.orderId())
                            .flatMap(payment -> Mono.<Void>error(
                                    new PaymentAlreadyProcessedException(
                                            """
                                            Payment for order with id=%d already processed at %s
                                            """.formatted(
                                                    payment.getOrderId(),
                                                    payment.getCreatedAt()
                                            ),
                                            ExceptionCode.PAYMENT_ALREADY_PROCESSED
                                    )
                            ))
                            .then(paymentRepository.save(
                                    request.orderId(),
                                    request.money()
                            ))
                            .flatMap(payment ->
                                    balanceRepository.decreaseBalance(payment.getMoney().amount())
                                            .thenReturn(payment)
                            );
                });
    }

}
