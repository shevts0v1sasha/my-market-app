package ru.yandex.market.payment.application.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.domain.Money;
import ru.yandex.market.payment.repository.BalanceRepository;

@Service
@RequiredArgsConstructor
public class BalanceQueryService {

    private final BalanceRepository balanceRepository;

    public Mono<Money> getBalance() {
        return balanceRepository.getBalance()
                .map(Money::new);
    }
}
