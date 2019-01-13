package pk.cryptocurrency.notifr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;
import pk.cryptocurrency.notifr.domain.Alert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("polling")
public class AlertsWebSocketIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MarketDataService marketDataService;

    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();
    private final WebClient webClient = WebClient.builder().build();

    /**
     * prepares mocks for marketDataService
     * as marketDataService is an external service, we can't rely that it is functioning properly during our test
     */
    @BeforeEach
    public void mockMarketDataService() throws IOException {
        Mockito.doAnswer(returnTrades(this::usdTrades))
                .when(marketDataService)
                .getTrades(pair("BTC", "USD"), Mockito.any(), Mockito.any());
    }

    @Test
    public void alertsShouldBePublishedToWebSocketClients() {
        ReplayProcessor<Alert> received = ReplayProcessor.create();

        URI uri = UriComponentsBuilder.newInstance().scheme("ws").host("localhost").port(port).path("alerts").build().toUri();

        socketClient.execute(uri, (WebSocketSession session) -> {

            Flux<String> in = session
                    .receive()
                    .map(WebSocketMessage::getPayloadAsText);

            return session
                    .send(Mono.just(session.textMessage("some test")))
                    .thenMany(in)
                    .map(str -> {
                        try {
                            return objectMapper.readValue(str, Alert.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .doOnNext(received::onNext)
                    .then();

        }).subscribe();

        URI alertUri = UriComponentsBuilder.newInstance().scheme("http").host("localhost").port(port)
                .path("alert").queryParam("pair", "BTC-USD").queryParam("limit", 500)
                .build().toUri();

        this.webClient
                .put()
                .uri(alertUri)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();

        // 1 alert should be received
        // wait max 10 seconds
        received
                .take(1)
                .timeout(Duration.ofSeconds(10))
                .doOnNext(System.out::println)
                .blockLast();

    }

    private Trades usdTrades() {
        List<Trade> trades = Arrays.asList(
                trade("BTC", "USD", BigDecimal.valueOf(400), Instant.now(), 1),
                trade("BTC", "USD", BigDecimal.valueOf(500), Instant.now(), 2),
                trade("BTC", "USD", BigDecimal.valueOf(600), Instant.now(), 3)
        );
        return new Trades(trades);
    }

    private Trade trade(String baseCurrency, String counterCurrency, BigDecimal price, Instant timestamp, long tradeId) {
        return new Trade.Builder().currencyPair(new CurrencyPair(baseCurrency, counterCurrency))
                .price(price)
                .timestamp(Date.from(timestamp))
                .id(String.valueOf(tradeId))
                .build();
    }

    private CurrencyPair pair(String base, String counter) {
        return Mockito.argThat((CurrencyPair pair) ->
                base.equals(pair.base.getCurrencyCode())
                        && counter.equals(pair.counter.getCurrencyCode()));
    }

    private Answer returnTrades(Supplier<Trades> tradesSupplier) {
        return invocation -> tradesSupplier.get();
    }


}
