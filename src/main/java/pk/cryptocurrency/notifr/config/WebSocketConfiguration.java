package pk.cryptocurrency.notifr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import pk.cryptocurrency.notifr.handler.AlertsWebSocketHandler;

import java.util.Collections;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping webSocketHandlerMapping(AlertsWebSocketHandler webSocketHandler) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(Collections.singletonMap("/alerts", webSocketHandler));
        handlerMapping.setOrder(1);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
