package ru.yandex.marketapp.cart.domain;

public record CartId(long id) {
    public CartId {
        if (id < 0) {
            throw new IllegalArgumentException("Id must be positive");
        }
    }
}
