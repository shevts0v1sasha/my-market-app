package ru.yandex.marketapp.item.infrastructure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;
import ru.yandex.marketapp.config.PostgresTestContainer;
import ru.yandex.marketapp.item.domain.Sort;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsResponse;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers // включает junit extension testcontainers, оно находит поля container и управляет их запуском/остановкой
@ImportTestcontainers(PostgresTestContainer.class) // импортирует тестконтейнер из отдельного класса
@ActiveProfiles("test")
public class ItemQueryServiceTest {

    @Autowired
    private ItemQueryService itemQueryService;

    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        databaseClient.sql("DELETE FROM shop_order_items").then().block();
        databaseClient.sql("DELETE FROM shop_orders").then().block();
        databaseClient.sql("DELETE FROM cart_items").then().block();
        databaseClient.sql("DELETE FROM carts").then().block();
        databaseClient.sql("DELETE FROM items").then().block();
        databaseClient.sql("""
                        INSERT INTO items(title, description, img_path, price, count)
                        VALUES ('1', '1', '1', 5, 1),
                               ('2', '2', '2', 4, 2),
                               ('3', '3', '3', 3, 3),
                               ('4', '4', '4', 2, 4),
                               ('5', '5', '5', 1, 5)
                        """)
                .then()
                .block();
    }

    @ParameterizedTest
    @MethodSource("argumentsStream")
    void givenSortAndExpectedIds_whenQueryServiceFind_thenIdsInCorrectOrder(Sort sort, List<String> ids) {
        // given
        SearchItemsRequest request = new SearchItemsRequest(
                "", sort, 1, 6
        );

        // when
        StepVerifier.create(itemQueryService.find(request))
                .assertNext(searchItemsResponse -> {
                    assertThat(searchItemsResponse).isNotNull();
                    assertThat(searchItemsResponse.items().size()).isEqualTo(2);

                    assertThat(searchItemsResponse.items().get(0).size()).isEqualTo(3);
                    assertThat(searchItemsResponse.items().get(1).size()).isEqualTo(3);
                    Iterator<String> iterator = ids.iterator();

                    assertThat(searchItemsResponse.items().get(0).get(0).title()).isEqualTo(iterator.next());
                    assertThat(searchItemsResponse.items().get(0).get(1).title()).isEqualTo(iterator.next());
                    assertThat(searchItemsResponse.items().get(0).get(2).title()).isEqualTo(iterator.next());
                    assertThat(searchItemsResponse.items().get(1).get(0).title()).isEqualTo(iterator.next());
                    assertThat(searchItemsResponse.items().get(1).get(1).title()).isEqualTo(iterator.next());
                    assertThat(searchItemsResponse.items().get(1).get(2).title()).isEqualTo(iterator.next());
                })
                .verifyComplete();
    }

    static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of(Sort.ALPHA, List.of("1", "2", "3", "4", "5", "")),
                Arguments.of(Sort.PRICE, List.of("5", "4", "3", "2", "1", ""))
        );
    }

}
