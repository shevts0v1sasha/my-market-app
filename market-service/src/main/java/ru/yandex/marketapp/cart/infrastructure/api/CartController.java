package ru.yandex.marketapp.cart.infrastructure.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartItemAction;
import ru.yandex.marketapp.cart.infrastructure.service.query.CartQueryService;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final AddCartItemUseCase addCartItemUseCase;
    private final CartQueryService cartQueryService;

    @GetMapping({"/cart", "/cart/items"})
    public Mono<String> getCart(Model model) {
        return addCartAttributes(model)
                .thenReturn("cart");
    }

    @PostMapping("/cart/items")
    public Mono<String> changeItemCount(@RequestParam long id,
                                        @RequestParam CartItemAction action,
                                        Model model) {
        return addCartItemUseCase.handle(id, ChangeCartItemAction.valueOf(action.name()))
                .then(addCartAttributes(model))
                .thenReturn("cart");
    }

    private Mono<Void> addCartAttributes(Model model) {
        return cartQueryService.getCurrentCart()
                .doOnNext(response -> {
                    model.addAttribute("items", response.items());
                    model.addAttribute("total", response.total());
                })
                .then();
    }
}
