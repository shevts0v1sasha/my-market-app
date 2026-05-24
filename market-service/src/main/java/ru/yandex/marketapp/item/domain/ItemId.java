package ru.yandex.marketapp.item.domain;

public record ItemId(long id) {

    public ItemId {
        if (id < -1) {
            throw new IllegalArgumentException("ID must be >= -1");
        }
    }
}
