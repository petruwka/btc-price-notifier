package pk.cryptocurrency.notifr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import pk.cryptocurrency.notifr.handler.ws.AlertsWebSocketHandler;
import pk.cryptocurrency.notifr.handler.ws.ThresholdsWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping webSocketHandlerMapping(AlertsWebSocketHandler alertsWebSocketHandler,
                                                  ThresholdsWebSocketHandler thresholdsWebSocketHandler) {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        Map<String, WebSocketHandler> webSocketHandlers = new HashMap<>();
        webSocketHandlers.put("/alerts", alertsWebSocketHandler);
        webSocketHandlers.put("/thresholds", thresholdsWebSocketHandler);
        handlerMapping.setUrlMap(webSocketHandlers);
        handlerMapping.setOrder(1);
        return handlerMapping;
    }

    @Bean
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
