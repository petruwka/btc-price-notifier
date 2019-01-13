package pk.cryptocurrency.notifr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.util.UriComponentsBuilder;
import pk.cryptocurrency.notifr.domain.Alert;
import pk.cryptocurrency.notifr.domain.CurrencyPair;
import pk.cryptocurrency.notifr.domain.Threshold;
import pk.cryptocurrency.notifr.service.AlertService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.util.stream.Stream;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlertsWebSocketIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AlertService alertService;

    private final WebSocketClient socketClient = new ReactorNettyWebSocketClient();

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


        // TODO: for now just sendAlert manually
        Stream.of(10, 20, 30)
                .map(v -> new Alert(new Threshold(new CurrencyPair("BTC", "USD"), BigDecimal.valueOf(v))))
                .forEach(alertService::sendAlert);


        // 3 alert should be received
        // wait max 10 seconds
        received
                .take(3)
                .timeout(Duration.ofSeconds(10))
                .doOnNext(System.out::println)
                .blockLast();

    }
}
