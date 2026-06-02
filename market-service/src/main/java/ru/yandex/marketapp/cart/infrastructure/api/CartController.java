package ru.yandex.marketapp.cart.infrastructure.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public Mono<String> getCart(@RequestParam(required = false) String paymentError, Model model) {
        return addCartAttributes(model, paymentError)
                .thenReturn("cart");
    }

    @PostMapping("/cart/items/{id}")
    public Mono<String> changeItemCount(@PathVariable long id,
                                        @RequestParam CartItemAction action,
                                        Model model) {
        return addCartItemUseCase.handle(id, ChangeCartItemAction.valueOf(action.name()))
                .then(addCartAttributes(model, null))
                .thenReturn("cart");
    }

    private Mono<Void> addCartAttributes(Model model, String paymentError) {
        return cartQueryService.getCurrentCart()
                .doOnNext(response -> {
                    model.addAttribute("items", response.items());
                    model.addAttribute("total", response.total());
                    model.addAttribute("balance", response.balanceRubles());
                    model.addAttribute("canBuy", response.canBuy());
                    model.addAttribute("paymentServiceAvailable", response.paymentServiceAvailable());
                    model.addAttribute("paymentMessage", response.paymentMessage());
                    if (paymentError != null) {
                        model.addAttribute("paymentMessage", mapPaymentError(paymentError));
                    }
                })
                .then();
    }

    private String mapPaymentError(String paymentError) {
        return switch (paymentError) {
            case "insufficient" -> "Недостаточно средств для оформления заказа";
            case "unavailable" -> "Сервис платежей недоступен";
            default -> "Не удалось оформить заказ";
        };
    }
}
