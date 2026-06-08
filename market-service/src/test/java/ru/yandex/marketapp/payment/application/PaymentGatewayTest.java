package ru.yandex.marketapp.payment.application;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import ru.yandex.market.payment.client.api.PaymentsApi;
import ru.yandex.market.payment.client.invoker.ApiClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentGatewayTest {

    private MockWebServer mockWebServer;
    private PaymentGateway paymentGateway;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        ApiClient apiClient = new ApiClient(WebClient.builder().build());
        apiClient.setBasePath(mockWebServer.url("/").toString());
        paymentGateway = new PaymentGateway(new PaymentsApi(apiClient));
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnBalanceFromPaymentService() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {"balance": 500000}
                        """));

        StepVerifier.create(paymentGateway.getBalanceKopecks())
                .assertNext(balance -> assertThat(balance).isEqualTo(500_000L))
                .verifyComplete();
    }

    @Test
    void shouldProcessPaymentThroughPaymentService() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "paymentId": 1,
                          "orderId": 10,
                          "amount": 100000,
                          "status": "SUCCESS"
                        }
                        """));

        StepVerifier.create(paymentGateway.processPayment(10L, 100_000L))
                .verifyComplete();
    }
}
