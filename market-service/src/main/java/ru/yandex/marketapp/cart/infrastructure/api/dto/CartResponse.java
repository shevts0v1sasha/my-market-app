package ru.yandex.marketapp.cart.infrastructure.api.dto;

import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;

import java.util.List;

public record CartResponse(List<ItemDto> items, long total) {
}
