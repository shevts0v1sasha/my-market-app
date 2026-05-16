package ru.yandex.marketapp.cart.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.common.application.NotFoundException;
import ru.yandex.marketapp.item.domain.ItemRepository;

@Service
@RequiredArgsConstructor
public class AddCartItemUseCase {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Mono<Void> handle(long itemId, ChangeCartItemAction action) {
        return itemRepository.findById(itemId)
                .switchIfEmpty(Mono.error(new NotFoundException("Item with id=%d not found".formatted(itemId))))
                .then(cartRepository.getCurrentCart())
                .flatMap(cart -> {
                    applyAction(itemId, action, cart);
                    return cartRepository.save(cart);
                })
                .then();
    }

    private void applyAction(long itemId, ChangeCartItemAction action, Cart cart) {
        switch (action) {
            case PLUS -> cart.increaseItem(itemId);
            case MINUS -> cart.decreaseItem(itemId);
            case DELETE -> cart.deleteItem(itemId);
        }
    }
}
