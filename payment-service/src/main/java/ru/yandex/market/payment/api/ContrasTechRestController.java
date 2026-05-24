package ru.yandex.market.payment.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.market.payment.api.model.ContrasTechCameraParams;

public class ContrasTechRestController implements ContrasTechApi {

    @Override
    public Mono<ResponseEntity<ContrasTechCameraParams>> getContrasTechParams(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok(new ContrasTechCameraParams()));
    }
}
