package ru.yandex.marketapp.item.domain;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemRepository {
    Mono<ItemsPage> find(ItemsSearchContext context);

    Mono<Item> findById(long id);

    Flux<Item> findByIds(List<Long> ids);

    Mono<Item> save(Item item);
}
