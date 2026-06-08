package ru.yandex.marketapp.cart.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartId;
import ru.yandex.marketapp.cart.domain.CartItem;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.cart.infrastructure.entity.CartEntity;
import ru.yandex.marketapp.cart.infrastructure.entity.CartItemEntity;
import ru.yandex.marketapp.config.CurrentUserService;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartR2dbcRepositoryAdapter implements CartRepository {

    private final CartR2dbcRepository cartR2dbcRepository;
    private final CartItemR2dbcRepository cartItemR2dbcRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public Mono<Cart> findCurrentCart() {
        return currentUserService.requireUserId()
                .flatMap(this::loadCartByUserId)
                .defaultIfEmpty(emptyCart());
    }

    @Override
    @Transactional
    public Mono<Cart> getCurrentCart() {
        return currentUserService.requireUserId()
                .flatMap(userId -> loadCartByUserId(userId)
                        .switchIfEmpty(createCart(userId)));
    }

    @Override
    @Transactional
    public Mono<Cart> save(Cart cart) {
        return currentUserService.requireUserId()
                .flatMap(userId -> {
                    List<CartItemEntity> items = cart.getItems().stream()
                            .map(item -> toEntity(item, cart.getId().id()))
                            .toList();

                    return cartItemR2dbcRepository.deleteByCartId(cart.getId().id())
                            .thenMany(cartItemR2dbcRepository.saveAll(items))
                            .then(Mono.just(cart));
                });
    }

    private Mono<Cart> loadCartByUserId(long userId) {
        return cartR2dbcRepository.findByUserId(userId)
                .flatMap(entity -> cartItemR2dbcRepository.findByCartId(entity.getId())
                        .map(item -> new CartItem(item.getItemId(), item.getAmount()))
                        .collectList()
                        .map(items -> new Cart(new CartId(entity.getId()), items)));
    }

    private Cart emptyCart() {
        return new Cart(new CartId(0L), List.of());
    }

    private Mono<Cart> createCart(long userId) {
        CartEntity entity = new CartEntity(null, userId);
        return cartR2dbcRepository.save(entity)
                .map(saved -> new Cart(new CartId(saved.getId()), List.of()));
    }

    private CartItemEntity toEntity(CartItem item, long cartId) {
        return new CartItemEntity(null, item.getItemId(), item.getAmount(), cartId);
    }
}
