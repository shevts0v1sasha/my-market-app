package ru.yandex.marketapp.cart.domain;

public interface CartRepository {
    Cart getCurrentCart();
    Cart save(Cart cart);
}
