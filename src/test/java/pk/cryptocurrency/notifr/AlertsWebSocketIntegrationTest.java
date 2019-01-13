package pk.cryptocurrency.notifr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
import java.net.URI;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AlertsWebSocketIntegrationTest {

    @LocalServerPort
    private int port;
    @Autowired
    private ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder().build();
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
}
