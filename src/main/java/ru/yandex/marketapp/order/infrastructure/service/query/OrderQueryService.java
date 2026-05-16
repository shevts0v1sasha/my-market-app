package ru.yandex.marketapp.order.infrastructure.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.marketapp.order.domain.OrderRepository;
import ru.yandex.marketapp.order.infrastructure.api.dto.OrderDto;
import ru.yandex.marketapp.order.infrastructure.mapper.OrderMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::map)
                .toList();
    }

    public Optional<OrderDto> findById(long id) {
        return orderRepository.findById(id)
                .map(orderMapper::map);
    }
}
