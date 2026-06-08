package ru.yandex.marketapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import reactor.core.publisher.Mono;

@TestConfiguration
public class TestOAuth2ClientConfig {

    @Bean
    ReactiveClientRegistrationRepository reactiveClientRegistrationRepository() {
        ClientRegistration registration = ClientRegistration.withRegistrationId("payment-service")
                .tokenUri("http://localhost:9000/oauth2/token")
                .clientId("market-service")
                .clientSecret("market-service-secret")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("payment.read", "payment.write")
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean
    @Primary
    ReactiveOAuth2AuthorizedClientManager testAuthorizedClientManager() {
        return authorizeRequest -> Mono.empty();
    }
}
