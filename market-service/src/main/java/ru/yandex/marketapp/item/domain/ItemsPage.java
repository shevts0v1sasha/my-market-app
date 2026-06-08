package ru.yandex.marketapp.item.domain;

import java.util.List;

public record ItemsPage(List<Item> items,
                        String search,
                        Sort sort,
                        Paging paging) {
}
