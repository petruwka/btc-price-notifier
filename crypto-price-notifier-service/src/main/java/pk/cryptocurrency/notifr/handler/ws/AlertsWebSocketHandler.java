package pk.cryptocurrency.notifr.handler.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.cryptocurrency.notifr.domain.Alert;
import pk.cryptocurrency.notifr.service.AlertService;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class AlertsWebSocketHandler extends AbstractWebSocketHandler<Alert> {

    private AlertService alertService;

    public AlertsWebSocketHandler(ObjectMapper objectMapper, AlertService alertService) {
        super(objectMapper);
        this.alertService = alertService;
    }

    @Override
    protected Flux<Alert> getStream() {
        return alertService.streamAlerts();
    }
}