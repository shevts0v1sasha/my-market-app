package ru.yandex.marketapp.order.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.marketapp.order.domain.OrderRepository;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderDto;
import ru.yandex.marketapp.order.infrastructure.mapper.OrderMapper;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Flux<OrderDto> findAll() {
        return orderRepository.findAll()
                .map(orderMapper::map);
    }

    public Mono<OrderDto> findById(long id) {
        return orderRepository.findById(id)
                .map(orderMapper::map);
    }
}
