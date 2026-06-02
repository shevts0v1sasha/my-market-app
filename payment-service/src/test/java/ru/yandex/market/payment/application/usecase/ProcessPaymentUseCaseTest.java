package ru.yandex.market.payment.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.market.payment.domain.Money;
import ru.yandex.market.payment.domain.Payment;
import ru.yandex.market.payment.exception.ExceptionCode;
import ru.yandex.market.payment.exception.IllegalMoneyException;
import ru.yandex.market.payment.exception.PaymentAlreadyProcessedException;
import ru.yandex.market.payment.repository.BalanceRepository;
import ru.yandex.market.payment.repository.PaymentRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUseCaseTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private ProcessPaymentUseCase processPaymentUseCase;

    @BeforeEach
    void setUp() {
        when(balanceRepository.getBalance()).thenReturn(Mono.just(500_000L));
    }

    @Test
    void shouldProcessPaymentWhenBalanceIsEnough() {
        ProcessPaymentRequest request = new ProcessPaymentRequest(1L, new Money(100_000L));
        Payment payment = new Payment(new Money(100_000L), 1L);

        when(paymentRepository.findByOrderId(1L)).thenReturn(Mono.empty());
        when(paymentRepository.save(1L, request.money())).thenReturn(Mono.just(payment));
        when(balanceRepository.decreaseBalance(100_000L)).thenReturn(Mono.just(400_000L));

        StepVerifier.create(processPaymentUseCase.handle(request))
                .expectNextMatches(result -> result.getOrderId().equals(1L))
                .verifyComplete();

        verify(balanceRepository).decreaseBalance(100_000L);
    }

    @Test
    void shouldFailWhenBalanceIsNotEnough() {
        ProcessPaymentRequest request = new ProcessPaymentRequest(1L, new Money(600_000L));

        StepVerifier.create(processPaymentUseCase.handle(request))
                .expectErrorSatisfies(error -> {
                    IllegalMoneyException exception = (IllegalMoneyException) error;
                    org.assertj.core.api.Assertions.assertThat(exception.getCode())
                            .isEqualTo(ExceptionCode.INSUFFICIENT_FUNDS);
                })
                .verify();

        verify(paymentRepository, never()).save(anyLong(), any());
    }

    @Test
    void shouldFailWhenPaymentAlreadyProcessed() {
        ProcessPaymentRequest request = new ProcessPaymentRequest(1L, new Money(100_000L));
        Payment existing = new Payment(new Money(100_000L), 1L);

        when(paymentRepository.findByOrderId(1L)).thenReturn(Mono.just(existing));

        StepVerifier.create(processPaymentUseCase.handle(request))
                .expectError(PaymentAlreadyProcessedException.class)
                .verify();

        verify(balanceRepository, never()).decreaseBalance(anyLong());
    }
}
