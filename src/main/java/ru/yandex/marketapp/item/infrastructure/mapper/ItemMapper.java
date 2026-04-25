package ru.yandex.marketapp.item.infrastructure.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;

@Component
public class ItemMapper {

    public ItemDto map(Item item) {
        return map(item, item.getCount());
    }

    public ItemDto map(Item item, int count) {
        return new ItemDto(
                item.getId().id(),
                item.getTitle(),
                item.getDescription(),
                item.getImgPath(),
                item.getPrice().price(),
                count
        );
    }

    public ItemDto createEmptyDto() {
        return new ItemDto(-1, "", "", null, 0, 0);
    }
}
