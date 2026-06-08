# Магазин кошек

Интернет-магазин на Spring Boot с мультимодульной структурой:
- market-service - витрина, корзина, заказы (порт 8080)
- payment-service - REST сервис платежей (порт 8081)
- payment-api - OpenAPI контракт
- auth-service - OAuth2 Authorization Server, Client Credentials (порт 9000)

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
- Auth service: http://localhost:9000

## Тестовые пользователи

| Логин | Пароль |
|-------|--------|
| user1 | password |
| user2 | password |

Также доступна регистрация нового пользователя: http://localhost:8080/register

## OAuth2

- market-service получает JWT через Client Credentials
- payment-service принимает только запросы с валидным Bearer токеном
- Баланс и платежи привязаны к userId покупателя
