package pk.cryptocurrency.notifr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pk.cryptocurrency.notifr.domain.Alert;
import pk.cryptocurrency.notifr.service.ThresholdEvent;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.UnicastProcessor;

@Configuration
public class CommonConfiguration {

    @Bean
    public FluxProcessor<Alert, Alert> alertsPublisher() {
        return UnicastProcessor.create();
    }

    @Bean
    public FluxProcessor<ThresholdEvent, ThresholdEvent> thresholdsPublisher() {
        return EmitterProcessor.create();
    }
}
