package ru.yandex.marketapp.cart.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartId;
import ru.yandex.marketapp.cart.domain.CartItem;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.cart.infrastructure.jpa.CartItemJpaEntity;
import ru.yandex.marketapp.cart.infrastructure.jpa.CartJpaEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartJpaRepositoryAdapter implements CartRepository {

    private static final long CURRENT_CART_ID = 1L;

    private final CartJpaRepository cartJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Cart getCurrentCart() {
        return cartJpaRepository.findById(CURRENT_CART_ID)
                .map(this::toDomain)
                .orElseGet(() -> new Cart(new CartId(CURRENT_CART_ID), List.of()));
    }

    @Override
    @Transactional
    public Cart save(Cart cart) {
        CartJpaEntity entity = new CartJpaEntity(CURRENT_CART_ID);
        List<CartItemJpaEntity> items = cart.getItems().stream()
                .map(this::toJpa)
                .toList();
        items.forEach(i -> i.setCart(entity));
        entity.setItems(items);
        return toDomain(cartJpaRepository.save(entity));
    }

    private Cart toDomain(CartJpaEntity entity) {
        List<CartItem> items = entity.getItems().stream()
                .map(i -> new CartItem(i.getItemId(), i.getAmount()))
                .toList();
        return new Cart(new CartId(entity.getId()), items);
    }

    private CartItemJpaEntity toJpa(CartItem item) {
        return new CartItemJpaEntity(
                null,
                item.getItemId(),
                item.getAmount(),
                null
        );
    }
}
