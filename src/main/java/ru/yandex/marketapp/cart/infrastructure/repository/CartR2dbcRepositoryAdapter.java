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
import ru.yandex.marketapp.cart.infrastructure.entity.CartItemEntity;
import ru.yandex.marketapp.cart.infrastructure.entity.CartEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartR2dbcRepositoryAdapter implements CartRepository {

    private static final long CURRENT_CART_ID = 1L;

    private final CartR2dbcRepository cartR2dbcRepository;
    private final CartItemR2dbcRepository cartItemR2dbcRepository;
    private final DatabaseClient databaseClient;

    @Override
    @Transactional(readOnly = true)
    public Mono<Cart> getCurrentCart() {
        return cartR2dbcRepository.findById(CURRENT_CART_ID)
                .flatMap(entity -> cartItemR2dbcRepository.findByCartId(entity.getId())
                        .map(i -> new CartItem(i.getItemId(), i.getAmount()))
                        .collectList()
                        .map(items -> new Cart(new CartId(entity.getId()), items)))
                .switchIfEmpty(Mono.just(new Cart(new CartId(CURRENT_CART_ID), List.of())));
    }

    @Override
    @Transactional
    public Mono<Cart> save(Cart cart) {
        CartEntity entity = new CartEntity(CURRENT_CART_ID);
        List<CartItemEntity> items = cart.getItems().stream()
                .map(this::toJpa)
                .toList();
        items.forEach(i -> i.setCartId(CURRENT_CART_ID));

        return databaseClient.sql("INSERT INTO carts(id) VALUES(:id) ON CONFLICT (id) DO NOTHING")
                .bind("id", entity.getId())
                .then()
                .then(cartItemR2dbcRepository.deleteByCartId(CURRENT_CART_ID))
                .thenMany(cartItemR2dbcRepository.saveAll(items))
                .then(Mono.just(cart));
    }

    private CartItemEntity toJpa(CartItem item) {
        return new CartItemEntity(
                null,
                item.getItemId(),
                item.getAmount(),
                CURRENT_CART_ID
        );
    }
}
