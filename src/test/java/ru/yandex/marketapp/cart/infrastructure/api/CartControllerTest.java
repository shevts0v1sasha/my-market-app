package ru.yandex.marketapp.cart.infrastructure.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartResponse;
import ru.yandex.marketapp.cart.infrastructure.service.query.CartQueryService;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        when(cartQueryService.getCurrentCart()).thenReturn(response);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", response.items()))
                .andExpect(model().attribute("total", response.total()));
    }

    @Test
    void shouldChangeCartItemAndRenderCartPage() throws Exception {
        CartResponse response = new CartResponse(List.of(), 0L);
        doNothing().when(addCartItemUseCase).handle(1L, ChangeCartItemAction.PLUS);
        when(cartQueryService.getCurrentCart()).thenReturn(response);

        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("items", response.items()))
                .andExpect(model().attribute("total", response.total()));
    }
}
