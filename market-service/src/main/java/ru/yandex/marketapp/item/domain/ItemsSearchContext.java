package ru.yandex.marketapp.item.domain;

public record ItemsSearchContext(String search, int pageNumber, int pageSize, Sort sort) {

    public ItemsSearchContext {
        if (search == null) {
            throw new IllegalArgumentException("Search must not be null");
        }
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be > 0");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be > 0");
        }

        if (sort == null) {
            throw new IllegalArgumentException("Sort must not be null");
        }

    }
}
