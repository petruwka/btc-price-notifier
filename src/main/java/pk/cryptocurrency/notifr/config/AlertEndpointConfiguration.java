package pk.cryptocurrency.notifr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import pk.cryptocurrency.notifr.handler.AlertHandler;

@Configuration
public class AlertEndpointConfiguration {

    @Bean
    public RouterFunction<ServerResponse> routes(AlertHandler alertHandler) {
        return RouterFunctions.route()
                .PUT("/alert", alertHandler::defineAlert)
                .DELETE("/alert", alertHandler::removeAlert)
                .build();
    }
}
