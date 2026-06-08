package ru.yandex.marketapp.cart.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.cart.domain.CartRepository;
import ru.yandex.marketapp.cart.infrastructure.api.dto.CartResponse;
import ru.yandex.marketapp.config.CurrentUserService;
import ru.yandex.marketapp.item.domain.ItemRepository;
import ru.yandex.marketapp.item.infrastructure.api.dto.ItemDto;
import ru.yandex.marketapp.item.infrastructure.mapper.ItemMapper;
import ru.yandex.marketapp.payment.application.PaymentGateway;
import ru.yandex.marketapp.payment.exception.PaymentServiceUnavailableException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartQueryService {

    private final CurrentUserService currentUserService;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final PaymentGateway paymentGateway;

    @Transactional(readOnly = false)
    public Mono<CartResponse> getCurrentCart() {
        return currentUserService.requireUserId()
                .flatMap(userId -> cartRepository.getCurrentCart()
                        .flatMap(cart -> {
                            var countsByItemId = cart.getItems().stream()
                                    .collect(Collectors.toMap(
                                            cartItem -> cartItem.getItemId(),
                                            Function.identity(),
                                            (left, right) -> right));
                            return itemRepository.findByIds(countsByItemId.keySet().stream().toList())
                                    .map(item -> itemMapper.map(
                                            item,
                                            countsByItemId.get(item.getId().id()).getAmount()))
                                    .collectList()
                                    .flatMap(items -> {
                                        long total = items.stream()
                                                .mapToLong(item -> item.price() * item.count())
                                                .sum();
                                        return enrichWithPaymentInfo(userId, items, total);
                                    });
                        }));
    }

    private Mono<CartResponse> enrichWithPaymentInfo(
            long userId,
            List<ItemDto> items,
            long total
    ) {
        if (items.isEmpty()) {
            return Mono.just(new CartResponse(items, total, null, false, true, null));
        }

        return paymentGateway.getBalanceKopecks(userId)
                .map(balanceKopecks -> buildCartResponse(items, total, balanceKopecks, true))
                .onErrorResume(PaymentServiceUnavailableException.class,
                        error -> Mono.just(buildUnavailableCartResponse(items, total)));
    }

    private CartResponse buildCartResponse(
            List<ItemDto> items,
            long total,
            long balanceKopecks,
            boolean paymentServiceAvailable
    ) {
        long balanceRubles = balanceKopecks / 100;
        long totalKopecks = total * 100;
        boolean canBuy = paymentServiceAvailable && balanceKopecks >= totalKopecks;
        String paymentMessage = canBuy
                ? null
                : "Недостаточно средств на балансе для оформления заказа";
        return new CartResponse(items, total, balanceRubles, canBuy, paymentServiceAvailable, paymentMessage);
    }

    private CartResponse buildUnavailableCartResponse(
            List<ItemDto> items,
            long total
    ) {
        return new CartResponse(
                items,
                total,
                null,
                false,
                false,
                "Сервис платежей недоступен"
        );
    }
}
