package ru.yandex.marketapp.order.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.marketapp.order.application.usecase.BuyUseCase;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderDto;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderItemDto;
import ru.yandex.marketapp.order.infrastructure.service.query.OrderQueryService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderQueryService orderQueryService;

    @MockitoBean
    private BuyUseCase buyUseCase;

    @Test
    void shouldRenderOrdersPage() throws Exception {
        List<OrderDto> orders = List.of(new OrderDto(10L, List.of(), 500L));
        when(orderQueryService.findAll()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attribute("orders", orders));
    }

    @Test
    void shouldRenderSingleOrderPage() throws Exception {
        OrderDto order = new OrderDto(10L, List.of(new OrderItemDto(1L, "cat", 100L, 2)), 200L);
        when(orderQueryService.findById(10L)).thenReturn(Optional.of(order));

        mockMvc.perform(get("/orders/10").param("newOrder", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attribute("order", order))
                .andExpect(model().attribute("newOrder", true));
    }

    @Test
    void shouldRedirectAfterBuy() throws Exception {
        when(buyUseCase.handle()).thenReturn(12L);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/12?newOrder=true"));
    }
}
