package ru.yandex.marketapp.order.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.order.application.usecase.BuyUseCase;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderDto;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderItemDto;
import ru.yandex.marketapp.order.infrastructure.service.query.OrderQueryService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private BuyUseCase buyUseCase;

    @Test
    void shouldRenderOrdersPage() throws Exception {
        List<OrderDto> orders = List.of(new OrderDto(10L, List.of(), 500L));
        when(orderQueryService.findAll()).thenReturn(Flux.fromIterable(orders));

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRenderSingleOrderPage() throws Exception {
        OrderDto order = new OrderDto(10L, List.of(new OrderItemDto(1L, "cat", 100L, 2)), 200L);
        when(orderQueryService.findById(10L)).thenReturn(Mono.just(order));

        webTestClient.get()
                .uri("/orders/10?newOrder=true")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRedirectAfterBuy() throws Exception {
        when(buyUseCase.handle()).thenReturn(Mono.just(12L));

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/orders/12?newOrder=true");
    }
}
