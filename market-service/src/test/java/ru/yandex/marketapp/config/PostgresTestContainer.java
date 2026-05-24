package ru.yandex.marketapp.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

public final class PostgresTestContainer {

    @Container // Через @Testcontainers находим все контейнеры и стартуем после запуска тестов
    @ServiceConnection // Сам подставляет проперти вместо @DynamicPropertySource
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");
}
