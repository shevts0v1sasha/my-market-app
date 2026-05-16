package ru.yandex.marketapp.cart.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartResponse;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.infrastructure.mapper.ItemMapper;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {

    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public CartResponse getCurrentCart() {
        var cart = cartRepository.getCurrentCart();
        var countsByItemId = cart.getItems().stream()
                .collect(Collectors.toMap(i -> i.getItemId(), Function.identity(), (left, right) -> right));
        var items = itemRepository.findByIds(
                        countsByItemId.keySet().stream().toList()
                ).stream()
                .map(item -> itemMapper.map(item, countsByItemId.get(item.getId().id()).getAmount()))
                .toList();
        long total = items.stream()
                .mapToLong(item -> item.price() * item.count())
                .sum();
        return new CartResponse(items, total);
    }
}
