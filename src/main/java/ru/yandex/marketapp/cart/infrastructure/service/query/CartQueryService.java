package ru.yandex.marketapp.cart.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
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

    public Mono<CartResponse> getCurrentCart() {
        return cartRepository.getCurrentCart()
                .flatMap(cart -> {
                    var countsByItemId = cart.getItems().stream()
                            .collect(Collectors.toMap(i -> i.getItemId(), Function.identity(), (left, right) -> right));
                    return itemRepository.findByIds(countsByItemId.keySet().stream().toList())
                            .map(item -> itemMapper.map(item, countsByItemId.get(item.getId().id()).getAmount()))
                            .collectList()
                            .map(items -> {
                                long total = items.stream()
                                        .mapToLong(item -> item.price() * item.count())
                                        .sum();
                                return new CartResponse(items, total);
                            });
                });
    }
}
