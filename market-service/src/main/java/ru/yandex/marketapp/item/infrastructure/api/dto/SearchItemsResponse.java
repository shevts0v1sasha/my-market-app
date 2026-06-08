package ru.yandex.marketapp.item.infrastructure.api.dto;

import ru.yandex.marketapp.item.domain.Paging;
import ru.yandex.marketapp.item.domain.Sort;

import java.util.List;

public record SearchItemsResponse(List<List<ItemDto>> items,
                                  String search,
                                  Sort sort,
                                  Paging paging) {
}
