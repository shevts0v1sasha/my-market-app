package ru.yandex.marketapp.cart.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
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
    private final CartItemJpaRepository cartItemJpaRepository;
    private final DatabaseClient databaseClient;

    @Override
    @Transactional(readOnly = true)
    public Mono<Cart> getCurrentCart() {
        return cartJpaRepository.findById(CURRENT_CART_ID)
                .flatMap(entity -> cartItemJpaRepository.findByCartId(entity.getId())
                        .map(i -> new CartItem(i.getItemId(), i.getAmount()))
                        .collectList()
                        .map(items -> new Cart(new CartId(entity.getId()), items)))
                .switchIfEmpty(Mono.just(new Cart(new CartId(CURRENT_CART_ID), List.of())));
    }

    @Override
    @Transactional
    public Mono<Cart> save(Cart cart) {
        CartJpaEntity entity = new CartJpaEntity(CURRENT_CART_ID);
        List<CartItemJpaEntity> items = cart.getItems().stream()
                .map(this::toJpa)
                .toList();
        items.forEach(i -> i.setCartId(CURRENT_CART_ID));

        return databaseClient.sql("INSERT INTO carts(id) VALUES(:id) ON CONFLICT (id) DO NOTHING")
                .bind("id", entity.getId())
                .then()
                .then(cartItemJpaRepository.deleteByCartId(CURRENT_CART_ID))
                .thenMany(cartItemJpaRepository.saveAll(items))
                .then(Mono.just(cart));
    }

    private CartItemJpaEntity toJpa(CartItem item) {
        return new CartItemJpaEntity(
                null,
                item.getItemId(),
                item.getAmount(),
                CURRENT_CART_ID
        );
    }
}
