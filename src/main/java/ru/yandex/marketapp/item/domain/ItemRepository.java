package ru.yandex.marketapp.item.domain;

public interface ItemRepository {
    ItemsPage find(ItemsSearchContext context);
}
