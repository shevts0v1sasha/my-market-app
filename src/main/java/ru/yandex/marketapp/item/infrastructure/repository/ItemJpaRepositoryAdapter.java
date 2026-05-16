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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemJpaRepositoryAdapter implements ItemRepository {

    private final ItemJpaRepository jpaRepository;

    @Override
    public ItemsPage find(ItemsSearchContext context) {
        Sort sorting = switch (context.sort()) {
            case ALPHA -> Sort.by("title").ascending();
            case PRICE -> Sort.by("price").ascending();
            case NO -> Sort.unsorted();
        };

        Pageable pageable = PageRequest.of(context.pageNumber() - 1, context.pageSize(), sorting);

        Page<ItemJpaEntity> result = context.search().isBlank() ?
                jpaRepository.findAll(pageable) :
                jpaRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        context.search(),
                        context.search(),
                        pageable
                );

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

    @Override
    public Optional<Item> findById(long id) {
        return jpaRepository.findById(id)
                .map(this::toDomainEntity);
    }

    @Override
    public List<Item> findByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByIdIn(ids).stream()
                .map(this::toDomainEntity)
                .sorted(Comparator.comparingLong(left -> left.getId().id()))
                .toList();
    }

    @Override
    public Item save(Item item) {
        ItemJpaEntity jpaEntity = toJpaEntity(item);
        return toDomainEntity(jpaRepository.save(jpaEntity));
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

    private ItemJpaEntity toJpaEntity(Item item) {
        return new ItemJpaEntity(
                item.getId().id(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice().price(),
                item.getCount()
        );
    }
}
