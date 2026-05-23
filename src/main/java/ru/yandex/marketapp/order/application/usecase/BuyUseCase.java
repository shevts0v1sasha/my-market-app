package ru.yandex.marketapp.order.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.common.application.BusinessRuleException;
import ru.yandex.marketapp.common.application.NotFoundException;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.order.domain.Order;
import ru.yandex.marketapp.order.domain.OrderItem;
import ru.yandex.marketapp.order.domain.OrderRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyUseCase {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Mono<Long> handle() {
        return cartRepository.getCurrentCart()
                .flatMap(cart -> {
                    if (cart.isEmpty()) {
                        return Mono.error(new BusinessRuleException("Cart is empty"));
                    }
                    List<Long> itemIds = cart.getItems().stream()
                            .map(i -> i.getItemId())
                            .toList();

                    return itemRepository.findByIds(itemIds)
                            .map(item -> new OrderItem(
                                    item.getId().id(),
                                    item.getTitle(),
                                    item.getPrice().price(),
                                    cart.countFor(item.getId().id())
                            ))
                            .filter(item -> item.count() > 0)
                            .collectList()
                            .flatMap(orderItems -> createOrderAndClearCart(cart, orderItems));
                });
    }

    private Mono<Long> createOrderAndClearCart(Cart cart, List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            return Mono.error(new NotFoundException("No items from cart were found in catalog"));
        }

        return orderRepository.save(Order.create(orderItems))
                .flatMap(created -> {
                    cart.clear();
                    return cartRepository.save(cart)
                            .thenReturn(created.id().id());
                });
    }
}
