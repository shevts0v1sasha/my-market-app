package ru.yandex.marketapp.item.infrastructure.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

@WebFluxTest(ItemsRestController.class)
public class ItemsRestControllerTest {

    @MockitoBean
    private ItemQueryService itemQueryService;

    @MockitoBean
    private AddCartItemUseCase addCartItemUseCase;

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class ValidationTest {

        @Test
        void shouldThrowBadRequestOnInvalidPageNumberAndPageSize() throws Exception {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/")
                            .queryParam("pageSize", "-1")
                            .queryParam("pageNumber", "-1")
                            .build())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectHeader().contentType("application/json")
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("Validation exception")
                    .jsonPath("$.errors").isArray();
        }

    }
}
