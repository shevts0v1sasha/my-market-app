package ru.yandex.marketapp.item.domain;

public record Paging(int pageSize,
                     int pageNumber,
                     boolean hasPrevious,
                     boolean hasNext) {
}
