package ru.yandex.marketapp.order.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.config.PasswordEncoderConfig;
import ru.yandex.marketapp.config.SecurityConfig;
import ru.yandex.marketapp.order.application.usecase.BuyUseCase;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderDto;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderItemDto;
import ru.yandex.marketapp.order.infrastructure.service.query.OrderQueryService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(OrderController.class)
@Import({SecurityConfig.class, PasswordEncoderConfig.class})
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private BuyUseCase buyUseCase;

    @MockitoBean
    private ru.yandex.marketapp.user.infrastructure.security.R2dbcUserDetailsService userDetailsService;

    @Test
    void shouldRenderOrdersPage() {
        List<OrderDto> orders = List.of(new OrderDto(10L, List.of(), 500L));
        when(orderQueryService.findAll()).thenReturn(Flux.fromIterable(orders));

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser("user1"))
                .get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRenderSingleOrderPage() {
        OrderDto order = new OrderDto(10L, List.of(new OrderItemDto(1L, "cat", 100L, 2)), 200L);
        when(orderQueryService.findById(10L)).thenReturn(Mono.just(order));

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser("user1"))
                .get()
                .uri("/orders/10?newOrder=true")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRedirectAfterBuy() {
        when(buyUseCase.handle()).thenReturn(Mono.just(12L));

        webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser("user1"))
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/orders/12?newOrder=true");
    }
}
