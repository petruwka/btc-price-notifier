package pk.cryptocurrency.notifr.handler.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class AbstractWebSocketHandler<T>  implements WebSocketHandler {

    private ObjectMapper objectMapper;

    public AbstractWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .subscribe(message -> log.info("message from websocket: {}", message));
        return session.send(getStream()
                .map(this::asString)
                .map(session::textMessage)
        );
    }

    protected abstract Flux<T> getStream();

    private String asString(T obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
