package ru.yandex.marketapp.item.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.marketapp.cart.domain.Cart;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.item.domain.Item;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.domain.ItemsPage;
import ru.yandex.marketapp.item.domain.ItemsSearchContext;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsRequest;
import ru.yandex.marketapp.item.infrastructure.api.dto.SearchItemsResponse;
import ru.yandex.marketapp.item.infrastructure.mapper.ItemMapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemQueryService {

    private final CartRepository cartRepository;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    public SearchItemsResponse find(SearchItemsRequest request) {
        Cart cart = cartRepository.getCurrentCart();
        ItemsPage itemsPage = itemRepository.find(new ItemsSearchContext(
                request.getSearch(),
                request.getPageNumber(),
                request.getPageSize(),
                request.getSort())
        );

        List<List<ItemDto>> split = split(itemsPage.items(), cart);

        return new SearchItemsResponse(split, itemsPage.search(), itemsPage.sort(), itemsPage.paging());
    }

    public Optional<ItemDto> findById(long id) {
        Cart cart = cartRepository.getCurrentCart();
        return itemRepository.findById(id)
                .map(item -> itemMapper.map(item, cart.countFor(item.getId().id())));
    }

    private List<List<ItemDto>> split(List<Item> items, Cart cart) {
        List<List<ItemDto>> result = new ArrayList<>();
        int counter = 0;
        Iterator<Item> iterator = items.iterator();

        while (counter < items.size()) {
            List<ItemDto> row = new ArrayList<>(3);

            for (int i = 0; i < 3; i++) {
                if (iterator.hasNext()) {
                    Item item = iterator.next();
                    row.add(itemMapper.map(item, cart.countFor(item.getId().id())));
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
