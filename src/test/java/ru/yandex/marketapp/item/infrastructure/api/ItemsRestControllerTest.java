package ru.yandex.marketapp.item.infrastructure.api;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

import static org.hamcrest.Matchers.containsInAnyOrder;

@WebMvcTest(ItemsRestController.class)
public class ItemsRestControllerTest {

    @MockitoBean
    private ItemQueryService itemQueryService;

    @MockitoBean
    private AddCartItemUseCase addCartItemUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class ValidationTest {

        @Test
        void shouldThrowBadRequestOnInvalidPageNumberAndPageSize() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/")
                            .param("pageSize", "-1")
                            .param("pageNumber", "-1"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message").exists())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                            .value("Validation exception"))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errors", containsInAnyOrder(
                            "pageSize: Page size must be >= 1",
                            "pageNumber: Page number must be >= 1"
                    )));
        }

    }
}
