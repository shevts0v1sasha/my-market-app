package ru.yandex.marketapp.order.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartId;
import ru.yandex.marketapp.cart.domain.CartItem;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemId;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.Price;
import ru.yandex.marketapp.order.domain.Order;
import ru.yandex.marketapp.order.domain.OrderId;
import ru.yandex.marketapp.order.domain.OrderItem;
import ru.yandex.marketapp.order.domain.OrderRepository;
import ru.yandex.marketapp.payment.application.PaymentGateway;
import ru.yandex.marketapp.payment.exception.PaymentInsufficientFundsException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyUseCaseTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private BuyUseCase buyUseCase;

    @Test
    void shouldCreateOrderPayAndCleanCart() {
        Cart cart = new Cart(new CartId(1L), List.of(
                new CartItem(1L, 2),
                new CartItem(2L, 1)
        ));
        when(cartRepository.getCurrentCart()).thenReturn(Mono.just(cart));
        Item first = new Item(new ItemId(1L), "a", "d", "/a.jpg", new Price(100L), 0);
        Item second = new Item(new ItemId(2L), "b", "d", "/b.jpg", new Price(200L), 0);
        when(itemRepository.findByIds(List.of(1L, 2L))).thenReturn(Flux.just(first, second));
        when(orderRepository.save(any(Order.class))).thenReturn(
                Mono.just(new Order(new OrderId(5L), List.of(
                        new OrderItem(1L, "a", 100L, 2),
                        new OrderItem(2L, "b", 200L, 1)
                ), 400L))
        );
        when(paymentGateway.processPayment(5L, 40_000L)).thenReturn(Mono.empty());
        when(cartRepository.save(cart)).thenReturn(Mono.just(cart));

        StepVerifier.create(buyUseCase.handle())
                .expectNext(5L)
                .verifyComplete();

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().totalSum()).isEqualTo(400L);
        assertThat(cart.isEmpty()).isTrue();
        verify(paymentGateway).processPayment(5L, 40_000L);
        verify(cartRepository).save(cart);
    }

    @Test
    void shouldDeleteOrderWhenPaymentFails() {
        Cart cart = new Cart(new CartId(1L), List.of(new CartItem(1L, 1)));
        when(cartRepository.getCurrentCart()).thenReturn(Mono.just(cart));
        Item item = new Item(new ItemId(1L), "a", "d", "/a.jpg", new Price(100L), 0);
        when(itemRepository.findByIds(List.of(1L))).thenReturn(Flux.just(item));
        when(orderRepository.save(any(Order.class))).thenReturn(
                Mono.just(new Order(new OrderId(5L), List.of(new OrderItem(1L, "a", 100L, 1)), 100L))
        );
        when(paymentGateway.processPayment(anyLong(), anyLong()))
                .thenReturn(Mono.error(new PaymentInsufficientFundsException("fail", new RuntimeException())));
        when(orderRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        StepVerifier.create(buyUseCase.handle())
                .expectError(PaymentInsufficientFundsException.class)
                .verify();

        verify(orderRepository).deleteById(5L);
        verify(cartRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
