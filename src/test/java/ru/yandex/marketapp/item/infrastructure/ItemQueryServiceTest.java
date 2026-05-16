package ru.yandex.marketapp.item.infrastructure;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = "/test-data/insert-items.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@ActiveProfiles("test")
public class ItemQueryServiceTest {

    @Autowired
    private ItemQueryService itemQueryService;

    @ParameterizedTest
    @MethodSource("argumentsStream")
    void givenSortAndExpectedIds_whenQueryServiceFind_thenIdsInCorrectOrder(Sort sort, List<String> ids) {
        // given
        SearchItemsRequest request = new SearchItemsRequest(
                "", sort, 1, 6
        );

        // when
        SearchItemsResponse searchItemsResponse = itemQueryService.find(request);

        // then
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
    }

    static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of(Sort.ALPHA, List.of("1", "2", "3", "4", "5", "")),
                Arguments.of(Sort.PRICE, List.of("5", "4", "3", "2", "1", ""))
        );
    }

}
