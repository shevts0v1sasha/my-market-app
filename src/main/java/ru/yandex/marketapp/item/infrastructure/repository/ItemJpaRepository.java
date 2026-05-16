package ru.yandex.marketapp.item.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.marketapp.item.infrastructure.jpa.ItemJpaEntity;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {

    Page<ItemJpaEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title,
            String description,
            Pageable pageable
    );

    List<ItemJpaEntity> findByIdIn(List<Long> ids);
}
