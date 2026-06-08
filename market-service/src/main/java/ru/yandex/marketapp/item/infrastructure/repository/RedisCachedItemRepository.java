package ru.yandex.marketapp.item.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.config.RedisConfig.ItemCacheProperties;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.ItemsPage;
import ru.yandex.marketapp.item.domain.ItemsSearchContext;
import ru.yandex.marketapp.item.domain.Paging;
import ru.yandex.marketapp.item.domain.Sort;
import ru.yandex.marketapp.item.infrastructure.cache.ItemCacheMapper;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Primary
@Repository
@RequiredArgsConstructor
public class RedisCachedItemRepository implements ItemRepository {

    public static final String ALL_ITEMS_KEY = "items:all";
    public static final String ITEM_KEY_PREFIX = "item:";

    private final ItemR2dbcRepositoryAdapter delegate;
    private final ReactiveStringRedisTemplate redisTemplate;
    private final ItemCacheMapper itemCacheMapper;
    private final ItemCacheProperties cacheProperties;

    @Override
    public Mono<ItemsPage> find(ItemsSearchContext context) {
        return getAllItems()
                .map(items -> toItemsPage(filterAndSort(items, context), context));
    }

    @Override
    public Mono<Item> findById(long id) {
        String key = ITEM_KEY_PREFIX + id;
        return redisTemplate.opsForValue().get(key)
                .map(itemCacheMapper::deserialize)
                .switchIfEmpty(
                        delegate.findById(id)
                                .flatMap(item -> cacheItem(item).thenReturn(item))
                );
    }

    @Override
    public Flux<Item> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Flux.empty();
        }
        return Flux.fromIterable(ids)
                .flatMap(this::findById)
                .sort(Comparator.comparing(item -> item.getId().id()));
    }

    @Override
    public Mono<Item> save(Item item) {
        return delegate.save(item)
                .flatMap(saved -> invalidateAllItemsCache()
                        .then(cacheItem(saved))
                        .thenReturn(saved));
    }

    private Mono<List<Item>> getAllItems() {
        return redisTemplate.opsForValue().get(ALL_ITEMS_KEY)
                .map(itemCacheMapper::deserializeAll)
                .switchIfEmpty(
                        delegate.findAllItems()
                                .collectList()
                                .flatMap(items -> cacheAllItems(items).thenReturn(items))
                );
    }

    private Mono<Void> cacheAllItems(List<Item> items) {
        return redisTemplate.opsForValue()
                .set(ALL_ITEMS_KEY, itemCacheMapper.serializeAll(items), ttl())
                .then();
    }

    private Mono<Void> cacheItem(Item item) {
        return redisTemplate.opsForValue()
                .set(ITEM_KEY_PREFIX + item.getId().id(), itemCacheMapper.serialize(item), ttl())
                .then();
    }

    private Mono<Void> invalidateAllItemsCache() {
        return redisTemplate.delete(ALL_ITEMS_KEY).then();
    }

    private Duration ttl() {
        return Duration.ofMinutes(cacheProperties.ttlMinutes());
    }

    private List<Item> filterAndSort(List<Item> items, ItemsSearchContext context) {
        var stream = items.stream();
        if (!context.search().isBlank()) {
            String search = context.search().toLowerCase(Locale.ROOT);
            stream = stream.filter(item ->
                    item.getTitle().toLowerCase(Locale.ROOT).contains(search)
                            || item.getDescription().toLowerCase(Locale.ROOT).contains(search));
        }

        Comparator<Item> comparator = switch (context.sort()) {
            case ALPHA -> Comparator.comparing(Item::getTitle, String.CASE_INSENSITIVE_ORDER);
            case PRICE -> Comparator.comparing(item -> item.getPrice().price());
            case NO -> Comparator.comparing(item -> item.getId().id());
        };

        return stream.sorted(comparator).toList();
    }

    private ItemsPage toItemsPage(List<Item> items, ItemsSearchContext context) {
        int offset = (context.pageNumber() - 1) * context.pageSize();
        List<Item> pageItems = items.stream()
                .skip(offset)
                .limit(context.pageSize())
                .toList();
        boolean hasNext = offset + context.pageSize() < items.size();
        return new ItemsPage(
                pageItems,
                context.search(),
                context.sort(),
                new Paging(context.pageSize(), context.pageNumber(), context.pageNumber() > 1, hasNext)
        );
    }
}
