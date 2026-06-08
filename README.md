# Магазин кошек

Интернет магазин на Spring Boot с мультимодульной структурой:
- market-service - витрина, корзина, заказы (порт 8080)
- payment-service - REST сервис платежей (порт 8081)
- payment-api - OpenAPI контракт

## Сборка

```bash
./gradlew clean build
```

## Тесты

```bash
./gradlew test
```

## Запуск

```bash
docker compose up --build
```

- Витрина: http://localhost:8080
- Payment API: http://localhost:8081
