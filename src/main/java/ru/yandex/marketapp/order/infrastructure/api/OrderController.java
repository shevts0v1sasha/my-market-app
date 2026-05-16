package ru.yandex.marketapp.order.infrastructure.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.order.application.usecase.BuyUseCase;
import ru.yandex.marketapp.order.infrastructure.service.query.OrderQueryService;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderQueryService orderQueryService;
    private final BuyUseCase buyUseCase;

    @GetMapping("/orders")
    public Mono<String> orders(Model model) {
        return orderQueryService.findAll()
                .collectList()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .thenReturn("orders");
    }

    @GetMapping("/orders/{id}")
    public Mono<String> order(@PathVariable long id,
                              @RequestParam(defaultValue = "false") boolean newOrder,
                              Model model) {
        return orderQueryService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND)))
                .map(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                    return "order";
                });
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return buyUseCase.handle()
                .map(orderId -> "redirect:/orders/%d?newOrder=true".formatted(orderId));
    }
}
