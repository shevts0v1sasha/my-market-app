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
import ru.yandex.marketapp.cart.application.usecase.AddCartItemUseCase;
import ru.yandex.marketapp.cart.application.usecase.ChangeCartItemAction;
import ru.yandex.marketapp.item.domain.Sort;
import ru.yandex.marketapp.item.infrastructure.api.dto.ChangeItemAction;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsResponse;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;

@Controller
@RequiredArgsConstructor
public class ItemsRestController {

    private final ItemQueryService queryService;
    private final AddCartItemUseCase addCartItemUseCase;

    @GetMapping({"/", "/items"})
    public String find(@Valid @ModelAttribute SearchItemsRequest request, Model model) {
        SearchItemsResponse itemsPage = queryService.find(request);
        model.addAttribute("items", itemsPage.items());
        model.addAttribute("search", itemsPage.search());
        model.addAttribute("sort", itemsPage.sort());
        model.addAttribute("paging", itemsPage.paging());

        return "items";
    }

    @GetMapping("/items/{id}")
    public String getItem(@PathVariable long id, Model model) {
        ItemDto item = queryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping("/items/{id}")
    public String changeCountOnItemPage(@PathVariable long id,
                                        @RequestParam ChangeItemAction action,
                                        Model model) {
        addCartItemUseCase.handle(id, ChangeCartItemAction.valueOf(action.name()));
        ItemDto item = queryService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
        model.addAttribute("item", item);
        return "item";
    }

    @PostMapping("/items")
    public String changeCountOnItemsPage(@RequestParam long id,
                                         @RequestParam(defaultValue = "") String search,
                                         @RequestParam(defaultValue = "NO") Sort sort,
                                         @RequestParam(defaultValue = "1") int pageNumber,
                                         @RequestParam(defaultValue = "5") int pageSize,
                                         @RequestParam ChangeItemAction action) {
        addCartItemUseCase.handle(id, ChangeCartItemAction.valueOf(action.name()));
        return "redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d"
                .formatted(search, sort, pageNumber, pageSize);
    }
}
