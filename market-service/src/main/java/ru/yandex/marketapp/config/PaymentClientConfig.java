package ru.yandex.marketapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.market.payment.client.api.PaymentsApi;
import ru.yandex.market.payment.client.invoker.ApiClient;

@Configuration
public class PaymentClientConfig {

    @Bean
    ApiClient paymentApiClient(@Value("${payment-service.base-url}") String baseUrl,
                               WebClient.Builder webClientBuilder) {
        ApiClient apiClient = new ApiClient(webClientBuilder.build());
        apiClient.setBasePath(baseUrl);
        return apiClient;
    }

    @Bean
    PaymentsApi paymentsApi(ApiClient paymentApiClient) {
        return new PaymentsApi(paymentApiClient);
    }
}
