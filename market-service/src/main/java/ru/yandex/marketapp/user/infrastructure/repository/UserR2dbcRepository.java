package ru.yandex.marketapp.user.infrastructure.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.user.infrastructure.entity.UserEntity;

public interface UserR2dbcRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Mono<UserEntity> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}
