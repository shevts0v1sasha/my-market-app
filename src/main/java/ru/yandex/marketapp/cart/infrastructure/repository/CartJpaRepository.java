package ru.yandex.marketapp.cart.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.marketapp.cart.infrastructure.jpa.CartJpaEntity;

public interface CartJpaRepository extends JpaRepository<CartJpaEntity, Long> {
}
