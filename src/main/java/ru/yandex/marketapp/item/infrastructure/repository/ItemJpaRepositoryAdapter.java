package ru.yandex.marketapp.item.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemId;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.ItemsPage;
import ru.yandex.marketapp.item.domain.ItemsSearchContext;
import ru.yandex.marketapp.item.domain.Paging;
import ru.yandex.marketapp.item.domain.Price;
import ru.yandex.marketapp.item.infrastructure.jpa.ItemJpaEntity;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class ItemJpaRepositoryAdapter implements ItemRepository {

    private final ItemJpaRepository jpaRepository;

    @Override
    public ItemsPage find(ItemsSearchContext context) {
        Sort sorting = switch (context.sort()) {
            case NO -> Sort.by("title").ascending();
            case PRICE -> Sort.by("price").ascending();
            default -> Sort.unsorted();
        };

        Pageable pageable = PageRequest.of(context.pageNumber() - 1, context.pageSize(), sorting);

        Page<ItemJpaEntity> result = context.search().isBlank() ?
                jpaRepository.findAll(pageable) :
                jpaRepository.findByTitleIgnoreCase(context.search(), pageable);

        List<Item> items = result.get()
                .map(this::toDomainEntity)
                .toList();

        return new ItemsPage(
                items,
                context.search(),
                context.sort(),
                new Paging(
                        result.getSize(),
                        result.getNumber() + 1,
                        result.hasPrevious(),
                        result.hasNext()
                ));
    }

    private Item toDomainEntity(ItemJpaEntity jpaEntity) {
        return new Item(
                new ItemId(jpaEntity.getId()),
                jpaEntity.getTitle(),
                jpaEntity.getDescription(),
                jpaEntity.getImgPath(),
                new Price(jpaEntity.getPrice()),
                jpaEntity.getCount()
        );
    }
}
