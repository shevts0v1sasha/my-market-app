package ru.yandex.marketapp.order.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartItem;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.common.application.BusinessRuleException;
import ru.yandex.marketapp.common.application.NotFoundException;
import ru.yandex.marketapp.config.CurrentUserService;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.order.domain.Order;
import ru.yandex.marketapp.order.domain.OrderItem;
import ru.yandex.marketapp.order.domain.OrderRepository;
import ru.yandex.marketapp.payment.application.PaymentGateway;
import ru.yandex.marketapp.payment.exception.PaymentException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyUseCase {

    private final CurrentUserService currentUserService;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;

    @Transactional
    public Mono<Long> handle() {
        return currentUserService.requireUserId()
                .flatMap(userId -> cartRepository.getCurrentCart()
                        .flatMap(cart -> processCart(userId, cart)));
    }

    private Mono<Long> processCart(long userId, Cart cart) {
        if (cart.isEmpty()) {
            return Mono.error(new BusinessRuleException("Cart is empty"));
        }
        List<Long> itemIds = cart.getItems().stream()
                .map(CartItem::getItemId)
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
                .flatMap(orderItems -> createOrderPayAndClearCart(userId, cart, orderItems));
    }

    private Mono<Long> createOrderPayAndClearCart(long userId, Cart cart, List<OrderItem> orderItems) {
        if (orderItems.isEmpty()) {
            return Mono.error(new NotFoundException("No items from cart were found in catalog"));
        }

        Order order = Order.create(orderItems);
        long totalKopecks = order.totalSum() * 100;

        return orderRepository.save(order, userId)
                .flatMap(created -> paymentGateway.processPayment(created.id().id(), totalKopecks, userId)
                        .thenReturn(created)
                        .onErrorResume(PaymentException.class, error ->
                                orderRepository.deleteById(created.id().id()).then(Mono.error(error)))
                )
                .flatMap(created -> clearCart(cart).thenReturn(created.id().id()));
    }

    private Mono<Void> clearCart(Cart cart) {
        cart.clear();
        return cartRepository.save(cart).then();
    }
}
