package ru.yandex.marketapp.item.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemId;
import ru.yandex.marketapp.item.domain.Price;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemCacheMapper {

    private static final TypeReference<List<ItemCacheEntry>> LIST_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;

    public String serializeAll(List<Item> items) {
        try {
            return objectMapper.writeValueAsString(items.stream().map(ItemCacheEntry::from).toList());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize items cache", e);
        }
    }

    public List<Item> deserializeAll(String json) {
        try {
            return objectMapper.readValue(json, LIST_TYPE).stream()
                    .map(ItemCacheEntry::toDomain)
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize items cache", e);
        }
    }

    public String serialize(Item item) {
        try {
            return objectMapper.writeValueAsString(ItemCacheEntry.from(item));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize item cache", e);
        }
    }

    public Item deserialize(String json) {
        try {
            return objectMapper.readValue(json, ItemCacheEntry.class).toDomain();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize item cache", e);
        }
    }

    private record ItemCacheEntry(
            Long id,
            String title,
            String description,
            String imgPath,
            long price,
            int count
    ) {
        static ItemCacheEntry from(Item item) {
            return new ItemCacheEntry(
                    item.getId().id(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getImgPath(),
                    item.getPrice().price(),
                    item.getCount()
            );
        }

        Item toDomain() {
            return new Item(
                    new ItemId(id),
                    title,
                    description,
                    imgPath,
                    new Price(price),
                    count
            );
        }
    }
}
