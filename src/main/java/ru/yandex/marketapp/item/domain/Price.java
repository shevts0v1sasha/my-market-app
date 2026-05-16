package ru.yandex.marketapp.item.domain;

public record Price(long price) {

    public Price {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be < 0");
        }
    }
}
