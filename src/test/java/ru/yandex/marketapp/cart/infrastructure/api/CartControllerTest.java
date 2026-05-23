package ru.yandex.marketapp.cart.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartResponse;
import ru.yandex.marketapp.cart.infrastructure.service.query.CartQueryService;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;

import java.util.List;

import static org.mockito.Mockito.when;
@WebFluxTest(CartController.class)
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AddCartItemUseCase addCartItemUseCase;

    @MockitoBean
    private CartQueryService cartQueryService;

    @Test
    void shouldRenderCartPage() throws Exception {
        CartResponse response = new CartResponse(
                List.of(new ItemDto(1L, "cat", "desc", "/cat.jpg", 100L, 2)),
                200L
        );
        when(cartQueryService.getCurrentCart()).thenReturn(Mono.just(response));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldChangeCartItemAndRenderCartPage() throws Exception {
        CartResponse response = new CartResponse(List.of(), 0L);
        when(addCartItemUseCase.handle(1L, ChangeCartItemAction.PLUS)).thenReturn(Mono.empty());
        when(cartQueryService.getCurrentCart()).thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "1")
                        .queryParam("action", "PLUS")
                        .build())
                .exchange()
                .expectStatus().isOk();
    }
}
