package ru.yandex.marketapp.cart.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public void handle(long itemId, ChangeCartItemAction action) {
        itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id=%d not found".formatted(itemId)));
        Cart cart = cartRepository.getCurrentCart();

        switch (action) {
            case PLUS -> cart.increaseItem(itemId);
            case MINUS -> cart.decreaseItem(itemId);
            case DELETE -> cart.deleteItem(itemId);
        }

        cartRepository.save(cart);
    }
}
