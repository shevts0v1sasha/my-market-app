package ru.yandex.marketapp.item.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.marketapp.item.infrastructure.entity.ItemEntity;

import java.util.List;

public interface ItemR2dbcRepository extends ReactiveCrudRepository<ItemEntity, Long> {

    Flux<ItemEntity> findByIdIn(List<Long> ids);
}
