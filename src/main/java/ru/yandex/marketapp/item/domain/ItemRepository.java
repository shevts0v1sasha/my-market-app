package ru.yandex.marketapp.item.domain;

import java.util.Optional;
import java.util.List;

public interface ItemRepository {
    ItemsPage find(ItemsSearchContext context);

    Optional<Item> findById(long id);

    List<Item> findByIds(List<Long> ids);

    Item save(Item item);
}
