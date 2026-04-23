package ru.yandex.marketapp.item.infrastructure.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.yandex.marketapp.item.domain.Sort;

@Getter
public class SearchItemsRequest {
    private String search = "";

    @NotNull
    private Sort sort = Sort.NO;

    @Min(1)
    private Integer pageNumber = 1;

    @Min(1)
    private Integer pageSize = 5;
}
