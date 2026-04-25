package ru.yandex.marketapp.order.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public long handle() {
        Cart cart = cartRepository.getCurrentCart();
        if (cart.isEmpty()) {
            throw new BusinessRuleException("Cart is empty");
        }
        List<Long> itemIds = cart.getItems().stream()
                .map(i -> i.getItemId())
                .toList();

        List<OrderItem> orderItems = itemRepository.findByIds(itemIds).stream()
                .map(item -> new OrderItem(
                        item.getId().id(),
                        item.getTitle(),
                        item.getPrice().price(),
                        cart.countFor(item.getId().id())
                ))
                .filter(item -> item.count() > 0)
                .toList();

        if (orderItems.isEmpty()) {
            throw new NotFoundException("No items from cart were found in catalog");
        }

        Order created = orderRepository.save(Order.create(orderItems));

        cart.clear();
        cartRepository.save(cart);

        return created.id().id();
    }
}
