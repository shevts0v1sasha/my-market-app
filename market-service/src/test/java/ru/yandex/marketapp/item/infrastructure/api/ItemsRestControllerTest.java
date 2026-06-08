package ru.yandex.marketapp.item.infrastructure.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.config.ExceptionControllerAdvice;
import ru.yandex.marketapp.config.PasswordEncoderConfig;
import ru.yandex.marketapp.config.SecurityConfig;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

import static org.mockito.Mockito.when;

@WebFluxTest(ItemsRestController.class)
@Import({SecurityConfig.class, PasswordEncoderConfig.class, ExceptionControllerAdvice.class})
public class ItemsRestControllerTest {

    @MockitoBean
    private ItemQueryService itemQueryService;

    @MockitoBean
    private AddCartItemUseCase addCartItemUseCase;

    @MockitoBean
    private ru.yandex.marketapp.user.infrastructure.security.R2dbcUserDetailsService userDetailsService;

    @Autowired
    private WebTestClient webTestClient;

    @Nested
    class ValidationTest {

        @Test
        void shouldThrowBadRequestOnInvalidPageNumberAndPageSize() {
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

        @Test
        void shouldChangeItemCountOnItemsPage() {
            when(addCartItemUseCase.handle(1L, ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction.PLUS))
                    .thenReturn(Mono.empty());

            webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser("user1"))
                    .mutateWith(SecurityMockServerConfigurers.csrf())
                    .post()
                    .uri(uriBuilder -> uriBuilder.path("/items/1")
                            .queryParam("action", "PLUS")
                            .queryParam("search", "")
                            .queryParam("sort", "NO")
                            .queryParam("pageNumber", "1")
                            .queryParam("pageSize", "5")
                            .build())
                    .exchange()
                    .expectStatus().is3xxRedirection()
                    .expectHeader().location("/items?search=&sort=NO&pageNumber=1&pageSize=5");
        }
    }
}
