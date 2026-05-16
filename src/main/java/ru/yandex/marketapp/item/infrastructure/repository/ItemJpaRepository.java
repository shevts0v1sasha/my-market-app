package ru.yandex.marketapp.item.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.marketapp.item.infrastructure.jpa.ItemJpaEntity;

import java.util.List;

public interface ItemJpaRepository extends ReactiveCrudRepository<ItemJpaEntity, Long> {

    Flux<ItemJpaEntity> findByIdIn(List<Long> ids);
}
