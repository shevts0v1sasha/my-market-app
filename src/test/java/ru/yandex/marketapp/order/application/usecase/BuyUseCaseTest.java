package ru.yandex.marketapp.order.application.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private BuyUseCase buyUseCase;

    @Test
    void shouldCreateOrderAndCleanCart() {
        Cart cart = new Cart(new CartId(1L), List.of(
                new CartItem(1L, 2),
                new CartItem(2L, 1)
        ));
        when(cartRepository.getCurrentCart()).thenReturn(cart);
        Item first = new Item(new ItemId(1L), "a", "d", "/a.jpg", new Price(100L), 0);
        Item second = new Item(new ItemId(2L), "b", "d", "/b.jpg", new Price(200L), 0);
        when(itemRepository.findByIds(List.of(1L, 2L))).thenReturn(List.of(first, second));
        when(orderRepository.save(any(Order.class))).thenReturn(
                new Order(new OrderId(5L), List.of(
                        new OrderItem(1L, "a", 100L, 2),
                        new OrderItem(2L, "b", 200L, 1)
                ), 400L)
        );

        long orderId = buyUseCase.handle();

        assertThat(orderId).isEqualTo(5L);
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().totalSum()).isEqualTo(400L);
        assertThat(captor.getValue().items()).hasSize(2);
        assertThat(cart.isEmpty()).isTrue();
        verify(cartRepository).save(cart);
    }
}
