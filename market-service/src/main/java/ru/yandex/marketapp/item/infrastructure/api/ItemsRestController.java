package ru.yandex.marketapp.item.infrastructure.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction;
import ru.yandex.marketapp.item.domain.Sort;
import ru.yandex.marketapp.item.infrastructure.api.dto.ChangeItemAction;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

@Controller
@RequiredArgsConstructor
public class ItemsRestController {

    private final ItemQueryService queryService;
    private final AddCartItemUseCase addCartItemUseCase;

    @GetMapping({"/", "/items"})
    public Mono<String> find(@Valid @ModelAttribute SearchItemsRequest request, Model model) {
        return queryService.find(request)
                .map(itemsPage -> {
                    model.addAttribute("items", itemsPage.items());
                    model.addAttribute("search", itemsPage.search());
                    model.addAttribute("sort", itemsPage.sort());
                    model.addAttribute("paging", itemsPage.paging());
                    return "items";
                });
    }

    @GetMapping("/items/{id}")
    public Mono<String> getItem(@PathVariable long id, Model model) {
        return queryService.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND)))
                .map(item -> {
                    model.addAttribute("item", item);
                    return "item";
                });
    }

    @PostMapping("/items/{id}")
    public Mono<String> changeCountOnItemPage(@PathVariable long id,
                                              @RequestParam ChangeItemAction action,
                                              @RequestParam(required = false) String search,
                                              @RequestParam(required = false) Sort sort,
                                              @RequestParam(required = false) Integer pageNumber,
                                              @RequestParam(required = false) Integer pageSize,
                                              Model model) {
        return addCartItemUseCase.handle(id, ChangeCartItemAction.valueOf(action.name()))
                .then(Mono.defer(() -> {
                    if (search != null && sort != null && pageNumber != null && pageSize != null) {
                        return Mono.just("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d"
                                .formatted(search, sort, pageNumber, pageSize));
                    }
                    return queryService.findById(id)
                            .switchIfEmpty(Mono.error(new ResponseStatusException(
                                    org.springframework.http.HttpStatus.NOT_FOUND)))
                            .map(item -> {
                                model.addAttribute("item", item);
                                return "item";
                            });
                }));
    }
}
