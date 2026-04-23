package ru.yandex.marketapp.item.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemId;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.ItemsPage;
import ru.yandex.marketapp.item.domain.ItemsSearchContext;
import ru.yandex.marketapp.item.domain.Price;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsResponse;
import ru.yandex.marketapp.item.infrastructure.mapper.ItemMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemQueryService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    public SearchItemsResponse find(SearchItemsRequest request) {
        ItemsPage itemsPage = itemRepository.find(new ItemsSearchContext(
                request.getSearch(),
                request.getPageNumber(),
                request.getPageSize(),
                request.getSort())
        );

        List<List<ItemDto>> split = split(itemsPage.items());

        return new SearchItemsResponse(split, itemsPage.search(), itemsPage.sort(), itemsPage.paging());
    }

    private List<List<ItemDto>> split(List<Item> items) {
        List<List<ItemDto>> result = new ArrayList<>();
        int counter = 0;
        Iterator<Item> iterator = items.iterator();

        while (counter < items.size()) {
            List<ItemDto> row = new ArrayList<>(3);

            for (int i = 0; i < 3; i++) {
                if (iterator.hasNext()) {
                    row.add(itemMapper.map(iterator.next()));
                } else {
                    row.add(itemMapper.createEmptyDto());
                }
                counter++;
            }

            result.add(row);
        }

        return result;
    }
}
