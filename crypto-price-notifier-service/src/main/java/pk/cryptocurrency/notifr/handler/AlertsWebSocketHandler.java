package pk.cryptocurrency.notifr.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import pk.cryptocurrency.notifr.domain.Alert;
import pk.cryptocurrency.notifr.service.AlertService;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AlertsWebSocketHandler implements WebSocketHandler {

    private AlertService alertService;
    private ObjectMapper objectMapper;

    public AlertsWebSocketHandler(AlertService alertService, ObjectMapper objectMapper) {
        this.alertService = alertService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .subscribe(message -> log.info("message from websocket: {}", message));
        return session.send(alertService.streamAlerts()
                .map(this::asString)
                .map(session::textMessage)
        );
    }

    private String asString(Alert alert) {
        try {
            return objectMapper.writeValueAsString(alert);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
