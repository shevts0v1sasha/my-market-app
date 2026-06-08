package ru.yandex.marketapp.item.infrastructure.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.marketapp.item.domain.Sort;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class SearchItemsRequest {

    private String search = "";

    private Sort sort = Sort.NO;

    @Min(value = 1, message = "Page number must be >= 1")
    private Integer pageNumber = 1;

    @Min(value = 1, message = "Page size must be >= 1")
    private Integer pageSize = 5;
}
