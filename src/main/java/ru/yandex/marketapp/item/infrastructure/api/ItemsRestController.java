package ru.yandex.marketapp.item.infrastructure.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsResponse;
import ru.yandex.marketapp.item.infrastructure.service.query.ItemQueryService;


@Controller
@RequiredArgsConstructor
public class ItemsRestController {

    private final ItemQueryService queryService;

    @GetMapping
    public String find(@Valid @ModelAttribute SearchItemsRequest request, Model model) {
        SearchItemsResponse itemsPage = queryService.find(request);
        model.addAttribute("itemsPage", itemsPage);

        return "/items";
    }
}
