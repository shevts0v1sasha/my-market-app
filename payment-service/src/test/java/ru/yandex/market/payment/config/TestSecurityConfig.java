package ru.yandex.market.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    ReactiveJwtDecoder reactiveJwtDecoder() {
        return token -> Mono.just(Jwt.withTokenValue(token)
                .header("alg", "none")
                .claim("sub", "market-service")
                .claim("scope", "payment.read payment.write")
                .build());
    }
}
