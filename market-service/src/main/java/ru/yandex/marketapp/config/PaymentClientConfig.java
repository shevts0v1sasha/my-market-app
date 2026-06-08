package ru.yandex.marketapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.market.payment.client.api.PaymentsApi;
import ru.yandex.market.payment.client.invoker.ApiClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    ApiClient paymentApiClient(@Value("${payment-service.base-url}") String baseUrl,
                               ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2ClientFilter) {
        WebClient webClient = WebClient.builder()
                .filter(oauth2ClientFilter)
                .build();
        ApiClient apiClient = new ApiClient(webClient);
        apiClient.setBasePath(baseUrl);
        return apiClient;
    }

    @Bean
    PaymentsApi paymentsApi(ApiClient paymentApiClient) {
        return new PaymentsApi(paymentApiClient);
    }
}
