package ru.yandex.market.payment.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.market.payment.repository.InMemoryBalanceRepository;
import ru.yandex.market.payment.repository.InMemoryPaymentRepository;

@SpringBootTest
@AutoConfigureWebTestClient
class PaymentV1RestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private InMemoryBalanceRepository balanceRepository;

    @Autowired
    private InMemoryPaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        balanceRepository.reset();
        paymentRepository.reset();
    }

    @Test
    void shouldReturnBalance() {
        webTestClient.get()
                .uri("/api/v1/payments/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(500_000);
    }

    @Test
    void shouldProcessPayment() {
        webTestClient.post()
                .uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "orderId": 42,
                          "amount": 100000
                        }
                        """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo(42)
                .jsonPath("$.amount").isEqualTo(100_000)
                .jsonPath("$.status").isEqualTo("SUCCESS");
    }

    @Test
    void shouldReturnConflictWhenInsufficientFunds() {
        webTestClient.post()
                .uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "orderId": 43,
                          "amount": 600000
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.code").isEqualTo("INSUFFICIENT_FUNDS");
    }
}
