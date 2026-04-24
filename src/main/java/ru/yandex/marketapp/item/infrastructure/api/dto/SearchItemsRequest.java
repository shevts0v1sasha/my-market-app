package ru.yandex.marketapp.item.infrastructure.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import ru.yandex.marketapp.item.domain.Sort;

@Getter
@Builder
public class SearchItemsRequest {

    @Builder.Default
    private String search = "";

    @NotNull
    @Builder.Default
    private Sort sort = Sort.NO;

    @Min(1)
    @Builder.Default
    private Integer pageNumber = 1;

    @Min(1)
    @Builder.Default
    private Integer pageSize = 5;
}
