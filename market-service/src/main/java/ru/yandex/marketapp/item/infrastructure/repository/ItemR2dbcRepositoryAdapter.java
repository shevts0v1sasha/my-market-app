package ru.yandex.marketapp.item.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemId;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.ItemsPage;
import ru.yandex.marketapp.item.domain.ItemsSearchContext;
import ru.yandex.marketapp.item.domain.Paging;
import ru.yandex.marketapp.item.domain.Price;
import ru.yandex.marketapp.item.infrastructure.entity.ItemEntity;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemR2dbcRepositoryAdapter implements ItemRepository {

    private final ItemR2dbcRepository r2dbcRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<ItemsPage> find(ItemsSearchContext context) {
        String where = context.search().isBlank()
                ? ""
                : "WHERE lower(title) LIKE :search OR lower(description) LIKE :search";
        String orderBy = switch (context.sort()) {
            case ALPHA -> "title ASC";
            case PRICE -> "price ASC";
            case NO -> "id ASC";
        };
        int offset = (context.pageNumber() - 1) * context.pageSize();

        var itemsSpec = databaseClient.sql("""
                        SELECT id, title, description, img_path, price, count
                        FROM items
                        %s
                        ORDER BY %s
                        LIMIT :limit OFFSET :offset
                        """.formatted(where, orderBy))
                .bind("limit", context.pageSize())
                .bind("offset", offset);

        var countSpec = databaseClient.sql("SELECT COUNT(*) AS total FROM items %s".formatted(where));

        if (!context.search().isBlank()) {
            String search = "%" + context.search().toLowerCase() + "%";
            itemsSpec = itemsSpec.bind("search", search);
            countSpec = countSpec.bind("search", search);
        }

        Mono<List<Item>> items = itemsSpec.map((row, metadata) -> toDomainEntity(new ItemEntity(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("description", String.class),
                        row.get("img_path", String.class),
                        row.get("price", Long.class),
                        row.get("count", Integer.class)
                )))
                .all()
                .collectList();
        Mono<Long> total = countSpec.map((row, metadata) -> row.get("total", Long.class))
                .one()
                .defaultIfEmpty(0L);

        return Mono.zip(items, total)
                .map(result -> new ItemsPage(
                        result.getT1(),
                        context.search(),
                        context.sort(),
                        new Paging(
                                context.pageSize(),
                                context.pageNumber(),
                                context.pageNumber() > 1,
                                offset + context.pageSize() < result.getT2()
                        )
                ));
    }

    @Override
    public Mono<Item> findById(long id) {
        return r2dbcRepository.findById(id)
                .map(this::toDomainEntity);
    }

    @Override
    public Flux<Item> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return Flux.empty();
        }
        return r2dbcRepository.findByIdIn(ids)
                .map(this::toDomainEntity)
                .sort((left, right) -> Long.compare(left.getId().id(), right.getId().id()));
    }

    @Override
    public Mono<Item> save(Item item) {
        ItemEntity jpaEntity = toJpaEntity(item);
        return r2dbcRepository.save(jpaEntity)
                .map(this::toDomainEntity);
    }

    private Item toDomainEntity(ItemEntity jpaEntity) {
        return new Item(
                new ItemId(jpaEntity.getId()),
                jpaEntity.getTitle(),
                jpaEntity.getDescription(),
                jpaEntity.getImgPath(),
                new Price(jpaEntity.getPrice()),
                jpaEntity.getCount()
        );
    }

    private ItemEntity toJpaEntity(Item item) {
        return new ItemEntity(
                item.getId().id(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice().price(),
                item.getCount()
        );
    }
}
