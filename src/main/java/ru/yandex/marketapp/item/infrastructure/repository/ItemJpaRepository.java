package ru.yandex.marketapp.item.infrastructure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.marketapp.item.infrastructure.jpa.ItemJpaEntity;

public interface ItemJpaRepository extends JpaRepository<ItemJpaEntity, Long> {

    Page<ItemJpaEntity> findByTitleIgnoreCase(
            String title,
            Pageable pageable
    );
}
