package ru.yandex.marketapp.cart.infrastructure.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartItemAction;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartResponse;
import ru.yandex.marketapp.cart.infrastructure.service.query.CartQueryService;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final AddCartItemUseCase addCartItemUseCase;
    private final CartQueryService cartQueryService;

    @GetMapping({"/cart", "/cart/items"})
    public String getCart(Model model) {
        addCartAttributes(model);
        return "cart";
    }

    @PostMapping("/cart/items")
    public String changeItemCount(@RequestParam long id,
                                  @RequestParam CartItemAction action,
                                  Model model) {
        addCartItemUseCase.handle(id, ChangeCartItemAction.valueOf(action.name()));
        addCartAttributes(model);
        return "cart";
    }

    private void addCartAttributes(Model model) {
        CartResponse response = cartQueryService.getCurrentCart();
        model.addAttribute("items", response.items());
        model.addAttribute("total", response.total());
    }
}
