package ru.yandex.marketapp.order.infrastructure.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.marketapp.order.application.usecase.BuyUseCase;
import ru.yandex.marketapp.order.infrastructure.service.query.OrderQueryService;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final BuyUseCase buyUseCase;

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderQueryService.findAll());
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String order(@PathVariable long id,
                        @RequestParam(defaultValue = "false") boolean newOrder,
                        Model model) {
        var order = orderQueryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
        model.addAttribute("order", order);
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @PostMapping("/buy")
    public String buy() {
        long orderId = buyUseCase.handle();
        return "redirect:/orders/%d?newOrder=true".formatted(orderId);
    }
}
