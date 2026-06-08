package ru.yandex.marketapp.item.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ru.yandex.marketapp.config.PostgresTestContainer;
import ru.yandex.marketapp.config.RedisTestContainer;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.Sort;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.repository.RedisCachedItemRepository;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ImportTestcontainers({PostgresTestContainer.class, RedisTestContainer.class})
@ActiveProfiles("test")
class ItemCacheIntegrationTest {

    @Autowired
    private ItemQueryService itemQueryService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @Autowired
    private DatabaseClient databaseClient;

    private long insertedItemId;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getReactiveConnection().serverCommands().flushAll().block();
        databaseClient.sql("DELETE FROM shop_order_items").then().block();
        databaseClient.sql("DELETE FROM shop_orders").then().block();
        databaseClient.sql("DELETE FROM cart_items").then().block();
        databaseClient.sql("DELETE FROM carts").then().block();
        databaseClient.sql("DELETE FROM items").then().block();
        databaseClient.sql("ALTER SEQUENCE items_id_seq RESTART WITH 1").then().block();

        insertedItemId = databaseClient.sql("""
                        INSERT INTO items(title, description, img_path, price, count)
                        VALUES ('cached-item', 'cache-desc', '/cat.jpg', 100, 1)
                        RETURNING id
                        """)
                .map((row, metadata) -> row.get("id", Long.class))
                .one()
                .block();
    }

    @Test
    void shouldLoadItemsFromDatabaseAndStoreInRedisCache() {
        SearchItemsRequest request = new SearchItemsRequest("", Sort.NO, 1, 10);

        StepVerifier.create(itemQueryService.find(request))
                .assertNext(response -> assertThat(response.items()).isNotEmpty())
                .verifyComplete();

        StepVerifier.create(redisTemplate.hasKey(RedisCachedItemRepository.ALL_ITEMS_KEY))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReadItemByIdFromCacheAfterFirstLoad() {
        StepVerifier.create(itemRepository.findById(insertedItemId))
                .assertNext(item -> assertThat(item.getTitle()).isEqualTo("cached-item"))
                .verifyComplete();

        StepVerifier.create(redisTemplate.hasKey(
                        RedisCachedItemRepository.ITEM_KEY_PREFIX + insertedItemId))
                .expectNext(true)
                .verifyComplete();
    }
}
