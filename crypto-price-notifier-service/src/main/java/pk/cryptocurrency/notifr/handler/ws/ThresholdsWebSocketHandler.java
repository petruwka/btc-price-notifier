package pk.cryptocurrency.notifr.handler.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.cryptocurrency.notifr.service.ThresholdEvent;
import pk.cryptocurrency.notifr.service.ThresholdService;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class ThresholdsWebSocketHandler extends AbstractWebSocketHandler<ThresholdEvent> {

    private ThresholdService thresholdService;

    public ThresholdsWebSocketHandler(ObjectMapper objectMapper, ThresholdService thresholdService) {
        super(objectMapper);
        this.thresholdService = thresholdService;
    }

    @Override
    protected Flux<ThresholdEvent> getStream() {
        return thresholdService.streamThresholds();
    }
}
